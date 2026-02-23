package com.example.javafx;

import java.math.BigDecimal;
import java.sql.Timestamp;

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
    }

    public void logOut(ActionEvent event) throws Exception {
        loggedCustomer = null;
        SceneManager.setLoggedCustomer(loggedCustomer);
        SceneManager.switchTo("loginScreen.fxml");
    }
    
}
