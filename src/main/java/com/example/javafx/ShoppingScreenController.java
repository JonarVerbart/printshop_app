package com.example.javafx;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import com.example.jsonrw.JsonReader;
import com.example.jsonrw.JsonWriter;
import com.example.pojo.Item;
import com.example.pojo.Order;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class ShoppingScreenController extends BaseController {
    
    @FXML
    private ListView<String> productList;
    @FXML
    private ListView<String> liveReceipt;

    @FXML
    private ComboBox<String> sizesComboBox;
    @FXML
    private ComboBox<String> finishComboBox;
    @FXML
    private TextField quantityTextField;

    @FXML
    private TableView<Item> cartTableView;
    @FXML
    private TableColumn<Item, String> productColumn;
    @FXML
    private TableColumn<Item, String> quantityColumn;
    @FXML
    private TableColumn<Item, String> unitPriceColumn;

    Order newOrder;

    @FXML
    public void initialize() {
        System.out.println("Initializing Shopping Screen...");

        productColumn.setCellValueFactory(new PropertyValueFactory<>("fullDisplayName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        unitPriceColumn.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));

        newOrder = new Order();

        System.out.println("Shopping Screen initialized");
    }

    @Override
    public void initializeFromDb() {
        fillProductList();
    }

    public void logOut(ActionEvent event) throws Exception {
        loggedCustomer = null;
        SceneManager.setLoggedCustomer(loggedCustomer);
        SceneManager.switchTo("loginScreen.fxml");
    }

    public void addToCart() {
        Item cartItem = dbInterface.retrieveItem(
            productList.getSelectionModel().getSelectedItem(), 
            sizesComboBox.getSelectionModel().getSelectedItem(), 
            finishComboBox.getSelectionModel().getSelectedItem()
        );

        if (cartItem != null) {
            cartItem.setQuantity(Integer.valueOf(quantityTextField.getText()));
            cartItem.setFullDisplayName();
            cartTableView.getItems().add(cartItem);

            newOrder.addItem(cartItem);

            updateOrderCosts();
            updateLiveReceipt();

        } else {
            System.out.println("Item doesn't exist in database");
        }
    }

    public void fillProductList() {
        //ObservableList<String> productList = FXCollections.observableArrayList("Paper", "Canvas", "Plate");
        //shoppingScreenProducts.setItems(productList);
        productList.getItems().setAll(dbInterface.retrieveDistinctProducts());
    }

    public void fillSizesComboBox() {
        String productType = productList.getSelectionModel().getSelectedItem();
        sizesComboBox.getItems().setAll(dbInterface.retrieveDistinctSizes(productType));
    }

    public void fillFinishComboBox() {
        String productType = productList.getSelectionModel().getSelectedItem();
        finishComboBox.getItems().setAll(dbInterface.retrieveDistinctFinishes(productType));
    }

    public void updateComboBoxes() {
        fillSizesComboBox();
        fillFinishComboBox();

        sizesComboBox.getSelectionModel().clearSelection();
        sizesComboBox.setValue(null);

        finishComboBox.getSelectionModel().clearSelection();
        finishComboBox.setValue(null);

        // FIX (prompt text not re-loading): Use prompt-aware button cells
        sizesComboBox.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? sizesComboBox.getPromptText() : item);
            }
        });

        finishComboBox.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? finishComboBox.getPromptText() : item);
            }
        });
    }

    public void placeOrder() {
        newOrder.setCustomerId(loggedCustomer.getId());
        newOrder.setOrderClosed(false);
        newOrder.setOrderPlacedTimestamp(new Timestamp(System.currentTimeMillis()));

        dbInterface.insertOrder(newOrder);

        List<Item> orderItems = newOrder.getItems();
        orderItems.forEach(orderItem -> {
            dbInterface.insertOrderItem(newOrder.getId(), orderItem.getId(), orderItem.getQuantity());
        } );
        System.out.println("Order was placed");
    }

    public void updateOrderCosts() {
        BigDecimal vat = BigDecimal.ZERO;
        AtomicReference<BigDecimal> subTotal = new AtomicReference<>(BigDecimal.ZERO);

        List<Item> orderItems = newOrder.getItems();
        orderItems.forEach(orderItem -> {
            BigDecimal unitPrice = new BigDecimal(orderItem.getUnitPrice().toString());
            BigDecimal quantity = new BigDecimal(orderItem.getQuantity().toString());
            subTotal.set(subTotal.get().add(unitPrice.multiply(quantity).setScale(2, RoundingMode.HALF_UP)));
        });

        vat = subTotal.get().multiply(new BigDecimal(0.21)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subTotal.get().add(vat);

        newOrder.setSubTotalCost(subTotal.get());
        newOrder.setTotalVAT(vat);
        newOrder.setTotalCost(total);
    }

    public void updateLiveReceipt() {
        String[] receiptElements = new String[4];
        receiptElements[0] = "Subtotal: " + newOrder.getSubTotalCost();
        receiptElements[1] = "21% VAT: " + newOrder.getTotalVAT();
        receiptElements[2] = "----------------------------- +";
        receiptElements[3] = "Total: " + newOrder.getTotalCost();

        liveReceipt.getItems().setAll(receiptElements);
    }

    public void exportCartToJson() {
        FileChooserUtil fileChooser = new FileChooserUtil();
        JsonWriter jsonWriter = new JsonWriter();
        jsonWriter.writeToJsonFile(newOrder, fileChooser.saveFile(productList.getScene().getWindow(), "JSON File", "*.json"));
    }

    public void importCartFromJson() {
        FileChooserUtil fileChooser = new FileChooserUtil();
        JsonReader jsonReader = new JsonReader();
        Order loadedCart = jsonReader.loadCartFromJsonFile(fileChooser.loadFile(productList.getScene().getWindow(), "JSON File", "*.json"));
        if (loadedCart != null && loadedCart instanceof Order) {
            cartTableView.getItems().clear();
            loadedCart.getItems().forEach(item -> {
                cartTableView.getItems().add(item);
            });
            newOrder = loadedCart;
            updateLiveReceipt();
        }
    }

}
