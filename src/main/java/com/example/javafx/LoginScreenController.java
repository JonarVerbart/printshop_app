package com.example.javafx;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class LoginScreenController extends BaseController {
    
    @FXML
    private TextField email;
    @FXML
    private TextField password;

    @Override
    public void initializeFromDb() {
        // Auto runs after initialize() auto runs, to use for DB stuff that needs to run on initialize 
    }

    public void logIn(ActionEvent event) throws Exception {
        try {
            loggedCustomer = dbInterface.retrieveCustomer(password.getText(), email.getText());
            SceneManager.setLoggedCustomer(loggedCustomer);
        } catch(NullPointerException e) {
            System.out.println("No account with this email exists");
        }
        if(loggedCustomer != null) {
            SceneManager.switchTo("shoppingScreen.fxml");
        }
        password.setText(null);
    }

    public void switchToCreateAccountScreen() throws IOException {
        SceneManager.switchTo("createAccountScreen.fxml");
    }

}
