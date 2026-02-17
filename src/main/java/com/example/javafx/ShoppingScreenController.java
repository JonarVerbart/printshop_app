package com.example.javafx;

import com.example.pojo.Item;

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

    @FXML
    public void initialize() {
        fillProductList();
        fillSizesComboBox();
        fillFinishComboBox();
        fillSizesComboBox();

        productColumn.setCellValueFactory(new PropertyValueFactory<>("product"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        unitPriceColumn.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
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
        cartItem.setQuantity(quantityTextField.getText());

        cartTableView.getItems().add(cartItem);

        // cartTableView.getColumns().set(0, "testerdetest");  // probs set these per TableColumn
    }

    public void fillProductList() {
        //ObservableList<String> productList = FXCollections.observableArrayList("Paper", "Canvas", "Plate");
        //shoppingScreenProducts.setItems(productList);
        productList.getItems().setAll("Paper", "Canvas", "Glass");
        System.out.println("Initialising");
    }

    public void fillSizesComboBox() {
        sizesComboBox.getItems().setAll("10 x 15", "30 x 40", "100 x 150");
    }

    public void fillFinishComboBox() {
        finishComboBox.getItems().setAll("Mat", "High Gloss");
    }

}
