package com.example.javafx;

import java.io.IOException;
import java.sql.SQLException;

import com.example.csvrw.CsvImporter;
import com.example.database.ConnProvider;
import com.example.database.DbInterface;
import com.example.pojo.Customer;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class SceneManager {

   // private static ConnProvider conn = new ConnProvider();
    private static DbInterface dbInterface;

    private static Customer loggedCustomer;

    private static Stage stage;

    public static Boolean accountCreated = false;

    public static void init(Stage primaryStage) {
        stage = primaryStage;
        ConnProvider conn = new ConnProvider();
        dbInterface = new DbInterface(conn.getDataSource());
    }

    public static void inventoryCsvToDb() {
        CsvImporter csvImporter = new CsvImporter(dbInterface);
        csvImporter.inventoryCsvToDb();
    }

    public static void clearOrderItemTable() {
        try {
            dbInterface.clearOrderItemTable();;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public static void clearOrdersTable() {
        try {
            dbInterface.clearOrdersTable();;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public static void clearItemsTable() {
        try {
            dbInterface.clearItemsTable();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void setLoggedCustomer(Customer customer) {
        loggedCustomer = customer;
    }

    public static void switchTo(String nextFxml, String previousFxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(nextFxml));
        Parent root = loader.load();
        
        BaseController controller = loader.getController();
        controller.setLoggedCustomer(loggedCustomer);
        controller.setStage(stage, previousFxml);
        controller.setDbInterface(dbInterface);
        // Moved set logged customer from here
        controller.initializeFromDb();

        stage.setScene(new Scene(root));

        root.setFocusTraversable(true);
        root.requestFocus();
        root.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            Node target = (Node) event.getTarget();
            if (!target.isFocusTraversable()) {
                root.requestFocus();
            }
        });
        //stage.sizeToScene();
        //stage.centerOnScreen();
        stage.show();

        // Platform.runLater(() -> {
        //     stage.setWidth(stage.getWidth() + 0.1);
        //     stage.setHeight(stage.getHeight() + 0.1);
        // });

        // For debugging loggedCustomer
        try {
            System.out.println("\nloggedCustomer in Controller: " + controller.loggedCustomer.getEmail());
        } catch(NullPointerException e) {
            System.out.println("\n" + e.getMessage());
        }
        try {;
            System.out.println("loggedCustomer in SceneManager: " + loggedCustomer.getEmail());
        } catch(NullPointerException e) {
            System.out.println(e.getMessage());
        }
    }

}
