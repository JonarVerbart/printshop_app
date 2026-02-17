package com.example.pojo;

public class Item {
    
    private Integer id;
    private String product;
    private String size;
    private String finish;
    private String unitPrice;
    private String quantity;

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

    public Item(Integer id, String product, String size, String finish, String unitPrice, String quantity) {
        this.id = id;
        this.product = product;
        this.size = size;
        this.finish = finish;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
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

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

}
