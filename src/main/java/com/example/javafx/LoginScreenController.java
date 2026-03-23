package com.example.javafx;

import java.io.IOException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginScreenController extends BaseController {
    
    @FXML
    private TextField email;
    @FXML
    private TextField password;

    @Override
    public void setStage(Stage stage, String previousFxml) {
        this.stage = stage;

        stage.setMinWidth(200);
        stage.setMinHeight(400);
        
        stage.setWidth(stage.getWidth() + 0.1);
        stage.setHeight(stage.getHeight() + 0.1);
        
        Platform.runLater(stage::sizeToScene);
        Platform.runLater(stage::centerOnScreen);
        
        //Platform.runLater(stage::sizeToScene);

        // Optional: still handle button-based restore-down
        // stage.maximizedProperty().addListener((obs, wasMax, isMax) -> {
        //     if (wasMax && !isMax) {
        //         Platform.runLater(stage::sizeToScene);
        //         Platform.runLater(stage::centerOnScreen);

        //     }
        // });

    }

    @Override
    public void initializeFromDb() {
        // Auto runs after initialize() auto runs, to use for DB stuff that needs to run on initialize 
    }

    public void logIn(ActionEvent event) throws Exception {
        try {
            loggedCustomer = dbInterface.retrieveCustomer(password.getText(), email.getText());
            SceneManager.setLoggedCustomer(loggedCustomer);
        } catch(NullPointerException e) {
            System.out.println("\nNo account with this email exists");
        }
        if(loggedCustomer != null) {
            SceneManager.switchTo("shoppingScreen.fxml", "loginScreen.fxml");
        }
        password.setText(null);
    }

    public void switchToCreateAccountScreen() throws IOException {
        SceneManager.switchTo("createAccountScreen.fxml", "loginScreen.fxml");
    }

}
