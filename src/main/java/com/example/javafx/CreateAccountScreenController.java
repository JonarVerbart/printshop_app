package com.example.javafx;

import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.pojo.Customer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class CreateAccountScreenController extends BaseController {
    
    @FXML
    private TextField firstNameTextField;
    @FXML
    private TextField lastNameTextField;
    @FXML
    private TextField emailTextField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private TextField addressTextField;
    @FXML
    private TextField zipCodeTextField;
    @FXML
    private TextField cityTextField;
    @FXML
    private TextField phoneNumberTextField;

    @FXML
    private Text feedbackText;


    @Override
    public void setStage(Stage stage, String previousFxml) {
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

        String email = emailTextField.getText();
        String phoneNumber = phoneNumberTextField.getText();
        String firstName = firstNameTextField.getText();
        String lastName = lastNameTextField.getText();
        String address = addressTextField.getText();
        String zipCode = zipCodeTextField.getText();
        String city = cityTextField.getText();

        if (email.isBlank() || firstName.isBlank() || lastName.isBlank() || address.isBlank() || zipCode.isBlank() || city.isBlank() || phoneNumber.isBlank() || passwordTextField.getText().isBlank()) {
            feedbackText.setText("All fields are required");
            feedbackText.setFill(Color.RED);
            feedbackText.setVisible(true);
            return;
        }

        Pattern emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
        Matcher emailMatcher = emailPattern.matcher(email);

        if (!emailMatcher.matches()) {
            feedbackText.setText("Invalid email address");
            feedbackText.setFill(Color.RED);
            feedbackText.setVisible(true);
            return;
        }

        if (passwordTextField.getText().length() < 8) {
            feedbackText.setText("Password must have 8 characters or more");
            feedbackText.setFill(Color.RED);
            feedbackText.setVisible(true);
            return;
        }

        Customer customer = new Customer(email, firstName, lastName, address, zipCode, city, phoneNumber);
        try {
            dbInterface.insertCustomer(customer, passwordTextField.getText());
            try {
                SceneManager.accountCreated = true;
                SceneManager.switchTo("loginScreen.fxml", "createAccountScreen.fxml");
            } catch(Exception e) {
                e.getMessage();
            }
        } catch(SQLException e) {
            System.out.println(e.getMessage());
            if (e.getErrorCode() == 1062) {
                feedbackText.setText("An account is already registered for " + emailTextField.getText());
                feedbackText.setFill(Color.RED);
                feedbackText.setVisible(true);
            }
        }
    }

    public void switchToLoginScreen() throws IOException {
        SceneManager.switchTo("loginScreen.fxml", "createAccountScreen.fxml");
    }

}
