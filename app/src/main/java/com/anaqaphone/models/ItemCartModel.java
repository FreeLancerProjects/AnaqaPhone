package com.anaqaphone.models;

import java.io.Serializable;

public class ItemCartModel implements Serializable {
    private String product_id;
    private String title;
    private double price;
    private int amount;
    private String sub_image;
    private String price_id;
    private String size_id;

    public ItemCartModel(String product_id, String title, double price, int amount, String sub_image, String price_id, String size_id) {
        this.product_id = product_id;
        this.title = title;
        this.price = price;
        this.amount = amount;
        this.sub_image = sub_image;
        this.price_id = price_id;
        this.size_id = size_id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
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

    public String getPrice_id() {
        return price_id;
    }

    public void setPrice_id(String price_id) {
        this.price_id = price_id;
    }

    public String getSize_id() {
        return size_id;
    }

    public void setSize_id(String size_id) {
        this.size_id = size_id;
    }
}
