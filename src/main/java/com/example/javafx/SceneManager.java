package com.example.javafx;

import java.io.IOException;

import com.example.database.ConnProvider;
import com.example.database.DbInterface;
import com.example.pojo.Customer;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {

   // private static ConnProvider conn = new ConnProvider();
    private static DbInterface dbInterface;

    private static Customer loggedCustomer;

    private static Stage stage;

    public static void init(Stage primaryStage) {
        stage = primaryStage;
        ConnProvider conn = new ConnProvider();
        dbInterface = new DbInterface(conn.getDataSource());
    }

    public static void setLoggedCustomer(Customer customer) {
        loggedCustomer = customer;
    }

    public static void switchTo(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxml));
        Parent root = loader.load();

        BaseController controller = loader.getController();
        controller.setDbInterface(dbInterface);
        controller.setLoggedCustomer(loggedCustomer);

        stage.setScene(new Scene(root));
        stage.show();
        try {
            System.out.println("loggedCustomer in Controller: " + controller.loggedCustomer.getEmail());
        } catch(NullPointerException e) {
            System.out.println(e.getMessage());
        }
        try {;
            System.out.println("loggedCustomer in SceneManager: " + loggedCustomer.getEmail());
        } catch(NullPointerException e) {
            System.out.println(e.getMessage());
        }
    }

}
