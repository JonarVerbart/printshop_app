package com.example.javafx;

import java.sql.Timestamp;
import java.util.List;

import com.example.pojo.Item;
import com.example.pojo.Order;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class ShoppingScreenController extends BaseController {
    
    @FXML
    private ListView<String> productList;

    @FXML
    private ComboBox<String> sizesComboBox;

    @FXML
    private ComboBox<String> finishComboBox;

    @FXML
    private TableView<Item> cartTableView;

    @FXML
    private TextField quantityTextField;

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
        //fillSizesComboBox();
        //fillFinishComboBox();
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
    }

}
