package com.example.pojo;

import java.time.Duration;

public class Item {
    
    private Integer id;
    private String product;
    private String size;
    private String finish;
    private String unitPrice;
    private Duration completionTime;
    private Integer quantity;
    private String fullDisplayName;

    // Default constructor for JsonReader
    public Item() {

    }

    public Item(String product, String size, String finish) {
        this.product = product;
        this.size = size;
        this.finish = finish;
    }

    public Item(Integer id, String product, String size, String finish, String unitPrice) {
        this.id = id;
        this.product = product;
        this.size = size;
        this.finish = finish;
        this.unitPrice = unitPrice;
    }

    public Item(Integer id, String product, String size, String finish, String unitPrice, Duration completionTime) {
        this.id = id;
        this.product = product;
        this.size = size;
        this.finish = finish;
        this.unitPrice = unitPrice;
        this.completionTime = completionTime;
    }

    public Item(String product, String size, String finish, String unitPrice, Duration completionTime) {
        this.product = product;
        this.size = size;
        this.finish = finish;
        this.unitPrice = unitPrice;
        this.completionTime = completionTime;
    }

    public void setFullDisplayName() {
        this.fullDisplayName = product + " " + size + " " + finish;
    }

    public String getFullDisplayName() {
        return fullDisplayName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProduct() {
        return this.product;
    }


    public String getSize() {
        return this.size;
    }


    public String getFinish() {
        return this.finish;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Duration getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(Duration completionTime) {
        this.completionTime = completionTime;
    }

}
