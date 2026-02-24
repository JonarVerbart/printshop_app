package com.example.javafx;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import com.example.pojo.TreeRowModel;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;

public class accountScreenController extends BaseController {

    @FXML
    private TreeTableView<TreeRowModel> ordersTreeTableView;
    @FXML
    private TreeTableColumn<TreeRowModel, String> orderTreeTableColumn;
    @FXML
    private TreeTableColumn<TreeRowModel, Timestamp> dateTreeTableColumn;
    @FXML
    private TreeTableColumn<TreeRowModel, String> statusTreeTableColumn;
    @FXML
    private TreeTableColumn<TreeRowModel, Timestamp> pickupTimeTreeTableColumn;
    @FXML
    private TreeTableColumn<TreeRowModel, BigDecimal> unitPriceTreeTableColumn;
    @FXML
    private TreeTableColumn<TreeRowModel, Integer> quantityTreeTableColumn;
    @FXML
    private TreeTableColumn<TreeRowModel, BigDecimal> subtotalTreeTableColumn;
    @FXML
    private TreeTableColumn<TreeRowModel, BigDecimal> vatTreeTableColumn;
    @FXML
    private TreeTableColumn<TreeRowModel, BigDecimal> totalCostTreeTableColumn;

    @FXML
    public void initialize() {

        orderTreeTableColumn.setCellValueFactory(param -> param.getValue().getValue().orderProperty());
        dateTreeTableColumn.setCellValueFactory(param -> param.getValue().getValue().dateProperty());
        statusTreeTableColumn.setCellValueFactory(param -> param.getValue().getValue().statusProperty());
        pickupTimeTreeTableColumn.setCellValueFactory(param -> param.getValue().getValue().pickupTimeProperty());
        unitPriceTreeTableColumn.setCellValueFactory(param -> param.getValue().getValue().unitPriceProperty());
        quantityTreeTableColumn.setCellValueFactory(param -> param.getValue().getValue().quantityProperty());
        subtotalTreeTableColumn.setCellValueFactory(param -> param.getValue().getValue().subtotalProperty());
        vatTreeTableColumn.setCellValueFactory(param -> param.getValue().getValue().vatProperty());
        totalCostTreeTableColumn.setCellValueFactory(param -> param.getValue().getValue().totalCostProperty());

    }

    @Override
    public void initializeFromDb() {

        //dbInterface.retrieveAllCustomerOrders(loggedCustomer.getEmail());
        fillOrderTable();
        /*
        TreeItem<TreeRowModel> treeRoot = new TreeItem<>(new TreeRowModel());
        
        
        for (int i = 0; i < 3; i++) {
            
            TreeRowModel orderRow = new TreeRowModel();
            orderRow.orderProperty().set("Test product " + i);
            orderRow.dateProperty().set(new Timestamp(System.currentTimeMillis()));

            TreeItem<TreeRowModel> orderItem = new TreeItem<>(orderRow);

            for (int j = 0; j < 2; j++) {
                TreeRowModel item = new TreeRowModel();
                item.quantityProperty().set(69 + j);
                item.statusProperty().set("Highest status");

                orderItem.getChildren().add(new TreeItem<>(item));
            }
            treeRoot.getChildren().add(orderItem);
        }
        ordersTreeTableView.setShowRoot(false);
        ordersTreeTableView.setRoot(treeRoot);
        treeRoot.getChildren().getFirst().setExpanded(true);
        */
    }

    public void logOut(ActionEvent event) throws Exception {
        loggedCustomer = null;
        SceneManager.setLoggedCustomer(loggedCustomer);
        SceneManager.switchTo("loginScreen.fxml");
    }

    public void fillOrderTable() {
        List<TreeRowModel> orderData = dbInterface.retrieveAllCustomerOrders(loggedCustomer.getEmail());
        List<TreeRowModel> orderItemData = dbInterface.retrieveAllCustomerOrderItems(loggedCustomer.getEmail());
        //List<TreeRowModel> orderData = 

        TreeItem<TreeRowModel> treeRoot = new TreeItem<>(new TreeRowModel());
        
        for (int i = 0; i < orderData.size(); i++) {
            TreeRowModel orderRow = new TreeRowModel();
            orderRow.orderProperty().set("Order# " + orderData.get(i).orderProperty().get());
            orderRow.dateProperty().set(orderData.get(i).dateProperty().getValue());
            orderRow.statusProperty().set(orderData.get(i).statusProperty().get());
            orderRow.pickupTimeProperty().set(orderData.get(i).pickupTimeProperty().getValue());
            orderRow.subtotalProperty().set(orderData.get(i).subtotalProperty().get());
            orderRow.vatProperty().set(orderData.get(i).vatProperty().get());
            orderRow.totalCostProperty().set(orderData.get(i).totalCostProperty().get());


            TreeItem<TreeRowModel> orderItem = new TreeItem<>(orderRow);
            // BUG: Every order gets all items from all orders
            for (int j = 0; j < orderItemData.size(); j++) {
                TreeRowModel itemRow = new TreeRowModel();
                itemRow.orderProperty().set(orderItemData.get(j).orderProperty().get());
                itemRow.pickupTimeProperty().set(orderItemData.get(j).pickupTimeProperty().getValue());
                itemRow.unitPriceProperty().set(orderItemData.get(j).unitPriceProperty().get());
                itemRow.quantityProperty().set(orderItemData.get(j).quantityProperty().get());

                orderItem.getChildren().add(new TreeItem<>(itemRow));
            }
            treeRoot.getChildren().add(orderItem);
        }
        ordersTreeTableView.setShowRoot(false);
        ordersTreeTableView.setRoot(treeRoot);
        treeRoot.getChildren().getFirst().setExpanded(true);
    }
    
}
