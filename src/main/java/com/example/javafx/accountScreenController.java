package com.example.javafx;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import com.example.pojo.TreeRowModel;
import com.example.util.OrderStatusProcessor;
import com.example.util.TimeFormatHandler;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.stage.Stage;

public class accountScreenController extends BaseController {

    @FXML
    private TreeTableView<TreeRowModel> ordersTreeTableView;
    @FXML
    private TreeTableColumn<TreeRowModel, String> orderTreeTableColumn;
    @FXML
    private TreeTableColumn<TreeRowModel, String> dateTreeTableColumn;
    @FXML
    private TreeTableColumn<TreeRowModel, String> statusTreeTableColumn;
    @FXML
    private TreeTableColumn<TreeRowModel, String> pickupTimeTreeTableColumn;
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
        orderTreeTableColumn.setCellValueFactory(param -> param.getValue().getValue().orderIdProperty());
        dateTreeTableColumn.setCellValueFactory(param -> param.getValue().getValue().timestampPlacedStringProperty());
        statusTreeTableColumn.setCellValueFactory(param -> param.getValue().getValue().statusProperty());
        pickupTimeTreeTableColumn.setCellValueFactory(param -> param.getValue().getValue().pickupTimeStringProperty());
        unitPriceTreeTableColumn.setCellValueFactory(param -> param.getValue().getValue().unitPriceProperty());
        quantityTreeTableColumn.setCellValueFactory(param -> param.getValue().getValue().quantityProperty());
        subtotalTreeTableColumn.setCellValueFactory(param -> param.getValue().getValue().subtotalProperty());
        vatTreeTableColumn.setCellValueFactory(param -> param.getValue().getValue().vatProperty());
        totalCostTreeTableColumn.setCellValueFactory(param -> param.getValue().getValue().totalCostProperty());
    }

    @Override
    public void setStage(Stage stage) {
        // TODO Auto-generated method stub
    }

    @Override
    public void initializeFromDb() {
        fillOrderTable();
    }

    public void logOut(ActionEvent event) throws Exception {
        loggedCustomer = null;
        SceneManager.setLoggedCustomer(loggedCustomer);
        SceneManager.switchTo("loginScreen.fxml");
    }

    public void fillOrderTable() {
        TimeFormatHandler timeFormatHandler = new TimeFormatHandler();
        OrderStatusProcessor orderStatusProcessor = new OrderStatusProcessor();
        
        List<TreeRowModel> orderData = dbInterface.retrieveAllCustomerOrders(loggedCustomer.getEmail());
        List<TreeRowModel> orderItemData = dbInterface.retrieveAllCustomerOrderItems(loggedCustomer.getEmail());

        TreeItem<TreeRowModel> treeRoot = new TreeItem<>(new TreeRowModel());
        
        for (int i = 0; i < orderData.size(); i++) {
            TreeRowModel orderRow = new TreeRowModel();
            orderRow.orderIdProperty().set("Order #: " + orderData.get(i).orderIdProperty().getValue());
            orderRow.timestampPlacedStringProperty().set(timeFormatHandler.timestampToLocalDateTime(orderData.get(i).timestampPlacedProperty().getValue()));
            orderRow.statusProperty().set(orderStatusProcessor.getStatusDisplayName(orderData.get(i).statusProperty().get()));
            orderRow.pickupTimeStringProperty().set(timeFormatHandler.timestampToLocalDateTime(orderData.get(i).pickupTimeProperty().getValue()));
            orderRow.subtotalProperty().set(orderData.get(i).subtotalProperty().get());
            orderRow.vatProperty().set(orderData.get(i).vatProperty().get());
            orderRow.totalCostProperty().set(orderData.get(i).totalCostProperty().get());

            TreeItem<TreeRowModel> orderParent = new TreeItem<>(orderRow);

            for (int j = 0; j < orderItemData.size(); j++) {
                if (orderData.get(i).orderIdProperty().getValue().equals(orderItemData.get(j).orderIdProperty().getValue())) {
                    TreeRowModel itemRow = new TreeRowModel();
                    itemRow.orderIdProperty().set(orderItemData.get(j).fullDisplayNameProperty().get());
                    //itemRow.pickupTimeProperty().set(orderItemData.get(j).pickupTimeProperty().getValue());
                    itemRow.unitPriceProperty().set(orderItemData.get(j).unitPriceProperty().get());
                    itemRow.quantityProperty().set(orderItemData.get(j).quantityProperty().get());

                    BigDecimal unitPrice = orderItemData.get(j).unitPriceProperty().get();
                    BigDecimal quantity = new BigDecimal(orderItemData.get(j).quantityProperty().get().toString());
                    BigDecimal combinedUnitPrice = unitPrice.multiply(quantity).setScale(2, RoundingMode.HALF_UP);
                    itemRow.subtotalProperty().set(combinedUnitPrice);

                    orderParent.getChildren().add(new TreeItem<>(itemRow));
                }
            }
            treeRoot.getChildren().add(orderParent);
        }
        ordersTreeTableView.setShowRoot(false);
        ordersTreeTableView.setRoot(treeRoot);
        treeRoot.getChildren().getFirst().setExpanded(true);
    }

    public void switchToShoppingScreen() throws IOException{
        SceneManager.switchTo("shoppingScreen.fxml");
    }
    
}
