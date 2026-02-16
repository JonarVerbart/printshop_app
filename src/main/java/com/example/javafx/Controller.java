package com.example.javafx;

//import java.io.IOException;
import java.sql.SQLException;

import com.example.database.ConnProvider;
import com.example.database.DbInterface;
import com.example.pojo.Customer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class Controller {
    
    private Stage stage;
    private Scene scene;
    //private Parent root;

    private ConnProvider conn = new ConnProvider();
    private DbInterface dbInterface = new DbInterface(conn.getDataSource());

    private Customer loggedCustomer; 

    @FXML
    private TextField accountScreenFirstName;
    @FXML
    private TextField accountScreenLastName;
    @FXML
    private TextField accountScreenEmail;
    @FXML
    private TextField accountScreenPassword;

    @FXML
    private TextField loginScreenEmail;
    @FXML
    private TextField loginScreenPassword;

    @FXML
    private ListView<String> shoppingScreenProducts;

    public void switchToScene1(ActionEvent event) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("loginScreen.fxml")); 
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToScene2(ActionEvent event) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("createAccountScreen.fxml")); 
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    // public void switchToScene3(ActionEvent event) throws Exception {
    //     Parent root = FXMLLoader.load(getClass().getResource("shoppingScreen.fxml")); 
    //     stage = (Stage)((Node)event.getSource()).getScene().getWindow();
    //     scene = new Scene(root);
    //     //fillList();
    //     stage.setScene(scene);
    //     stage.show();
    //     fillList();
    // }

    public void switchToScene3(ActionEvent event) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("shoppingscreen.fxml"));
        Parent root = loader.load();
        Controller shoppingController = loader.getController();
        shoppingController.fillList();
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        //fillList();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void createNewAccount(ActionEvent event) {
        Customer customer = new Customer(accountScreenEmail.getText(), accountScreenFirstName.getText(), accountScreenLastName.getText());
        try {
            dbInterface.insertCustomer(customer, accountScreenPassword.getText());
            try {
                switchToScene1(event);
            } catch(Exception e) {
                e.getMessage();
            }
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void logIn(ActionEvent event) throws Exception {
        try {
            loggedCustomer = dbInterface.retrieveCustomer(loginScreenPassword.getText(), loginScreenEmail.getText());
        } catch(NullPointerException e) {
            System.out.println("No account with this email exists");
        }
        if(loggedCustomer != null) {
            switchToScene3(event);
        }
        loginScreenPassword.setText(null);
    }

    public void logOut(ActionEvent event) throws Exception {
        loggedCustomer = null;
        switchToScene1(event);
    }

    public void fillList() {
        //ObservableList<String> productList = FXCollections.observableArrayList("Paper", "Canvas", "Plate");
        //shoppingScreenProducts.setItems(productList);
        shoppingScreenProducts.getItems().setAll("Paper", "Canvas", "Plate");
        System.out.println("Initialising");
    }

}
