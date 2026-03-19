package com.example.pojo;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * MySQL uses datetime type and looks like this: '2023-01-31 23:59:59.999999'
 * Figure out how to interface that between SQL and Java
 * 
 * You can just do this (importing java.sql.*):
 *  Timestamp ts = rs.getTimestamp(1);
        System.out.println("Timestamp: " + ts);
    
            will show: Timestamp: 2026-02-18 14:35:00.0


    I will need a junction table order_item that has order_id, item_id and quantity. Each item that is part of an order has a row. So there would be multiple rows of items for the same order_id (if multiple items with seperate item_id's are ordered).
    I will need to join order_item, orders and items (and maybe customers sometimes) to get the column combinations I need.
 */

public class Order {

    private Integer id;
    private Integer customerId;

    private List<Item> items = new ArrayList<>();
    // private ArrayList<String> itemsFullName;

    private Timestamp orderPlacedTimestamp; // Timestamp in Java is system time. In MySQL it is automatically converted to UTC.
    private Timestamp pickupTime;   // Timestamp in Java is system time. In MySQL it is automatically converted to UTC.
    private Duration totalCompletionTime;

    private BigDecimal subTotalCost;
    private BigDecimal totalVAT;
    private BigDecimal totalCost;

    private Integer status;

    private String orderNotes;


    public void addItem(Item item) {
        items.add(item);
    }

    public List<Item> getItems() {
        return List.copyOf(items);
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Timestamp getOrderPlacedTimestamp() {
        return this.orderPlacedTimestamp;
    }

    public void setOrderPlacedTimestamp(Timestamp orderPlacedTimestamp) {
        this.orderPlacedTimestamp = orderPlacedTimestamp;
    }

    public Timestamp getPickupTime() {
        return this.pickupTime;
    }

    public void setPickupTime(Timestamp pickupTime) {
        this.pickupTime = pickupTime;
    }

    public Duration getTotalCompletionTime() {
        return this.totalCompletionTime;
    }

    public void setTotalCompletionTime(Duration totalCompletionTime) {
        this.totalCompletionTime = totalCompletionTime;
    }

    public BigDecimal getSubTotalCost() {
        return this.subTotalCost;
    }

    public void setSubTotalCost(BigDecimal subTotalCost) {
        this.subTotalCost = subTotalCost;
    }

    public BigDecimal getTotalVAT() {
        return this.totalVAT;
    }

    public void setTotalVAT(BigDecimal totalVAT) {
        this.totalVAT = totalVAT;
    }

    public BigDecimal getTotalCost() {
        return this.totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getOrderNotes() {
        return orderNotes;
    }

    public void setOrderNotes(String orderNotes) {
        this.orderNotes = orderNotes;
    }

}
