/**
 * This class essentially functions as Main for the application. The real main in App.java just calls this.
 * It's so I can do stuff before and after loading the app if I want to.
 */

package com.example.javafx;

import com.example.constants.Constants;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Javafx extends Application{

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("PhotoShop Printshop");
        Image icon = new Image(getClass().getResourceAsStream("/PSIcon128.png"));
        primaryStage.getIcons().add(icon);

        SceneManager.init(primaryStage);
        SceneManager.switchTo("loginScreen.fxml", null);
        if (Constants.LOAD_ITEMS) {
            SceneManager.inventoryCsvToDb();
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if (Constants.CLEAR_ORDERS) {
                    SceneManager.clearOrderItemTable();
                    SceneManager.clearOrdersTable();
                }
                if (Constants.CLEAR_ITEMS) {
                    SceneManager.clearItemsTable();
                }
                System.out.println("\nApplication has shut down\n");
            } catch (Exception e) {
                e.getMessage();
            }
        }));
    }

}
