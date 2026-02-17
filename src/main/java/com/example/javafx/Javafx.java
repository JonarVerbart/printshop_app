/**
 * This class essentially functions as Main for the application. The real main in App.java just calls this.
 * It's so I can do stuff before and after loading the app if I want to.
 */

package com.example.javafx;

import javafx.application.Application;
import javafx.stage.Stage;

public class Javafx extends Application{

    @Override
    public void start(Stage primaryStage) throws Exception {
        SceneManager.init(primaryStage);
        SceneManager.switchTo("loginScreen.fxml");
        SceneManager.inventoryCsvToDb();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                SceneManager.clearItemTable();
                System.out.println("Application has shut down");
            } catch (Exception e) {
                e.getMessage();
            }
        }));
    }

    /*
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("loginScreen.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setTitle("PhotoShop Store");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    */
}
