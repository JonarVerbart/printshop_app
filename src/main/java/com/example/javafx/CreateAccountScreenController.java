package com.example.javafx;

import java.io.IOException;
import java.sql.SQLException;

import com.example.pojo.Customer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CreateAccountScreenController extends BaseController {
    
    @FXML
    private TextField firstName;
    @FXML
    private TextField lastName;
    @FXML
    private TextField email;
    @FXML
    private TextField password;
    @FXML
    private TextField address;
    @FXML
    private TextField zipCode;
    @FXML
    private TextField city;
    @FXML
    private TextField phoneNumber;


    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
        stage.setMinWidth(200);
        stage.setMinHeight(400);
    }

    @Override
    public void initializeFromDb() {
        // Auto runs after initialize() auto runs, to use for DB stuff that needs to run on initialize 
    }

    @FXML
    public void createNewAccount(ActionEvent event) {
        Customer customer = new Customer(email.getText(), firstName.getText(), lastName.getText(), address.getText(), zipCode.getText(), city.getText(), phoneNumber.getText());
        try {
            dbInterface.insertCustomer(customer, password.getText());
            try {
                SceneManager.switchTo("loginScreen.fxml");
            } catch(Exception e) {
                e.getMessage();
            }
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void switchToLoginScreen() throws IOException {
        SceneManager.switchTo("loginScreen.fxml");
    }

}
