package com.anaqaphone.models;

import java.io.Serializable;

public class ItemCartModel implements Serializable {
    private int product_id;
    private String title;
    private double price;
    private int amount;
    private String sub_image;

    public ItemCartModel(int product_id, String title, double price, int amount, String sub_image) {
        this.product_id = product_id;
        this.title = title;
        this.price = price;
        this.amount = amount;
        this.sub_image = sub_image;

    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public int getAmount() {
        return amount;
    }

    public String getSub_image() {
        return sub_image;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setSub_image(String sub_image) {
        this.sub_image = sub_image;
    }
}
