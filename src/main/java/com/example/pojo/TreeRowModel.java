package com.example.pojo;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TreeRowModel {
    
    private final StringProperty orderId = new SimpleStringProperty();
    private final StringProperty fullDisplayName = new SimpleStringProperty();
    private final ObjectProperty<Timestamp> timestampPlaced = new SimpleObjectProperty<>();
    private final StringProperty timestampPlacedString = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty();
    private final ObjectProperty<Timestamp> pickupTime = new SimpleObjectProperty<>();
    private final StringProperty pickupTimeString = new SimpleStringProperty();
    private final ObjectProperty<BigDecimal> unitPrice = new SimpleObjectProperty<>();
    private final ObjectProperty<Integer> quantity = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> subtotal = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> vat = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> totalCost = new SimpleObjectProperty<>();


    public StringProperty orderIdProperty() {
        return this.orderId;
    }

    public StringProperty fullDisplayNameProperty() {
        return this.fullDisplayName;
    }

    public ObjectProperty<Timestamp> timestampPlacedProperty() {
        return this.timestampPlaced;
    }

    public StringProperty timestampPlacedStringProperty() {
        return this.timestampPlacedString;
    }

    public StringProperty statusProperty() {
        return this.status;
    }

    public ObjectProperty<Timestamp> pickupTimeProperty() {
        return this.pickupTime;
    }

    public StringProperty pickupTimeStringProperty() {
        return this.pickupTimeString;
    }

    public ObjectProperty<BigDecimal> unitPriceProperty() {
        return this.unitPrice;
    }


    public ObjectProperty<Integer> quantityProperty() {
        return this.quantity;
    }


    public ObjectProperty<BigDecimal> subtotalProperty() {
        return this.subtotal;
    }


    public ObjectProperty<BigDecimal> vatProperty() {
        return this.vat;
    }


    public ObjectProperty<BigDecimal> totalCostProperty() {
        return this.totalCost;
    }

}
