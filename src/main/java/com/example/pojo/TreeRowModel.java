package com.example.pojo;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TreeRowModel {
    
    private final StringProperty order = new SimpleStringProperty();
    private final ObjectProperty<Timestamp> date = new SimpleObjectProperty<>();
    private final StringProperty status = new SimpleStringProperty();
    private final ObjectProperty<Timestamp> pickupTime = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> unitPrice = new SimpleObjectProperty<>();
    private final ObjectProperty<Integer> quantity = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> subtotal = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> vat = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> totalCost = new SimpleObjectProperty<>();


    public StringProperty orderProperty() {
        return this.order;
    }


    public ObjectProperty<Timestamp> dateProperty() {
        return this.date;
    }


    public StringProperty statusProperty() {
        return this.status;
    }


    public ObjectProperty<Timestamp> pickupTimeProperty() {
        return this.pickupTime;
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
