package com.example.javafx;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class EditAccountScreenController extends BaseController {

    @FXML
    private TextField emailTextField;
    @FXML
    private TextField phoneNumberTextField;
    @FXML
    private TextField firstNameTextField;
    @FXML
    private TextField lastNameTextField;
    @FXML
    private TextField addressTextField;
    @FXML
    private TextField zipCodeTextField;
    @FXML
    private TextField cityTextField;
    @FXML
    private PasswordField newPasswordField;

    @FXML
    private Text feedbackText;
    

    @Override
    public void setStage(Stage stage, String previousFxml) {
        emailTextField.setPromptText(loggedCustomer.getEmail());
        phoneNumberTextField.setPromptText(loggedCustomer.getPhoneNumber());
        firstNameTextField.setPromptText(loggedCustomer.getFirstname());
        lastNameTextField.setPromptText(loggedCustomer.getLastName());
        addressTextField.setPromptText(loggedCustomer.getAddress());
        zipCodeTextField.setPromptText(loggedCustomer.getZipCode());
        cityTextField.setPromptText(loggedCustomer.getCity());
        
    }

    @Override
    public void initializeFromDb() {
        // TODO Auto-generated method stub
        
    }

    public void logOut(ActionEvent event) throws Exception {
        loggedCustomer = null;
        SceneManager.setLoggedCustomer(loggedCustomer);
        SceneManager.switchTo("loginScreen.fxml", "shoppingScreen.fxml");
    }

    public void switchToAccountScreen() throws IOException{
        SceneManager.switchTo("accountScreen.fxml", "editAccountScreen.fxml");
    }

    // BUG Can't change details twice in same session if email has changed, because email is not updated. (consequence of bug below)
    public void saveAccountDetailChanges() {

        String email = emailTextField.getText();
        String phoneNumber = phoneNumberTextField.getText();
        String firstName = firstNameTextField.getText();
        String lastName = lastNameTextField.getText();
        String address = addressTextField.getText();
        String zipCode = zipCodeTextField.getText();
        String city = cityTextField.getText();
        
        email = (email.isBlank()) ? loggedCustomer.getEmail() : email;

        Pattern emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
        Matcher emailMatcher = emailPattern.matcher(email);

        if (!emailMatcher.matches()) {
            feedbackText.setText("Invalid email address");
            feedbackText.setFill(Color.RED);
            feedbackText.setVisible(true);
            return;
        }

        phoneNumber = (phoneNumber.isBlank()) ? loggedCustomer.getPhoneNumber() : phoneNumber;
        firstName = (firstName.isBlank()) ? loggedCustomer.getFirstname() : firstName;
        lastName = (lastName.isBlank()) ? loggedCustomer.getLastName() : lastName;
        address = (address.isBlank()) ? loggedCustomer.getAddress() : address;
        zipCode = (zipCode.isBlank()) ? loggedCustomer.getZipCode() : zipCode;
        city = (city.isBlank()) ? loggedCustomer.getCity() : city;
        // BUG: editAccountPage does not use updated info when reloaded without restarting app
        if (newPasswordField.getText().isBlank()) {
            if (approveWithoutPasswordChange()) {
                System.out.println("\nUpdating without new passsword...");
                dbInterface.updateCustomer(false, loggedCustomer.getEmail(), email, null, firstName, lastName, address, zipCode, city, phoneNumber);
                feedbackText.setText("Changes saved");
                feedbackText.setFill(Color.GREEN);
                feedbackText.setVisible(true);
            }
        } else {
            if (approveWithPasswordChange()) {
                System.out.println("\nUpdating with new password...");
                dbInterface.updateCustomer(true, loggedCustomer.getEmail(), email, dbInterface.plainToHashed(newPasswordField.getText()), firstName, lastName, address, zipCode, city, phoneNumber);
                feedbackText.setText("Changes saved");
                feedbackText.setFill(Color.GREEN);
                feedbackText.setVisible(true);
            }
        }
    }

    // public void approveAccountDetailChange() {
    //     Alert alert = new Alert(AlertType.CONFIRMATION);
    //     alert.setTitle("Change account details?");
    //     alert.setHeaderText("You have changed (some) account details.\nAre you sure you want to store them?");
    //     alert.setContentText("Select Yes or No");

        
    //     ButtonType yes = new ButtonType("Yes");
    //     ButtonType no = new ButtonType("No");
    //     alert.getButtonTypes().setAll(yes, no);

        
    //     Optional<ButtonType> result = alert.showAndWait();
    //     if (result.isPresent() && result.get() == yes) {
    //         System.out.println("User selected YES");
    //     } else {
    //         System.out.println("User selected NO");
    //     }

    // }

    public Boolean approveWithPasswordChange() {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Change password?");
        dialog.setHeaderText("You entered a new password. Your old password is required to save this change.\nAre you sure you want to save all changes?\n");

        ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(yesButton, noButton);

        PasswordField oldPasswordField = new PasswordField();
        oldPasswordField.setPromptText("Old password");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.add(new Label("Password:"), 0, 0);
        grid.add(oldPasswordField, 1, 0);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == yesButton) {
                return true;   // if user selected yes
                }
            return false;      // if user selected no
            });

        Optional<Boolean> result = dialog.showAndWait();

        if (result.isPresent() && result.get()) {
                System.out.println("\nUser pressed YES");
                try {
                    if (dbInterface.checkPassword(oldPasswordField.getText(), dbInterface.retrieveHashedPassword(loggedCustomer.getEmail()))) {
                        return true; // Selected yes and password correct
                    } else {
                        System.out.println("\nWrong password");
                        feedbackText.setText("Wrong password");
                        feedbackText.setFill(Color.RED);
                        feedbackText.setVisible(true);
                        return false;
                    }
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                System.out.println("\nUser pressed NO");
                feedbackText.setVisible(false);
                return false;
            }
            return false;
    }

    public Boolean approveWithoutPasswordChange() {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Save changes?");
        dialog.setHeaderText("Are you sure you want to save all changes?");

        ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(yesButton, noButton);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == yesButton) {
                return true;   // if user selected yes
                }
            return false;      // if user selected no
            });

        Optional<Boolean> result = dialog.showAndWait();

        if (result.isPresent() && result.get()) {
                System.out.println("\nUser pressed YES");
                return true;
            } else {
                System.out.println("\nUser pressed NO");
                feedbackText.setVisible(false);
                return false;
            }

    }
    
}
