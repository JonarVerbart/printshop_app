package com.example.javafx;

import java.io.IOException;
import java.sql.SQLException;

import com.example.pojo.Customer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

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
    public void createNewAccount(ActionEvent event) {
        Customer customer = new Customer(email.getText(), firstName.getText(), lastName.getText());
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
