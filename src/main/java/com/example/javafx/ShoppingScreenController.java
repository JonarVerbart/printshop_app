package com.example.javafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class ShoppingScreenController extends BaseController {
    
    @FXML
    private ListView<String> productList;

    @FXML
    public void initialize() {
        fillList();
    }

    public void logOut(ActionEvent event) throws Exception {
        loggedCustomer = null;
        SceneManager.setLoggedCustomer(loggedCustomer);
        SceneManager.switchTo("loginScreen.fxml");
    }

    public void fillList() {
        //ObservableList<String> productList = FXCollections.observableArrayList("Paper", "Canvas", "Plate");
        //shoppingScreenProducts.setItems(productList);
        productList.getItems().setAll("Paper", "Canvas", "Glass");
        System.out.println("Initialising");
    }

}
