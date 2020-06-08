package com.anaqaphone.models;

import java.io.Serializable;

public class ItemCartModel implements Serializable {
    private int item_id;
    private String title;
    private double cost;
    private int amount;
    private String sub_image;

    public ItemCartModel(int item_id, String title, double cost, int amount, String sub_image) {
        this.item_id = item_id;
        this.title = title;
        this.cost = cost;
        this.amount = amount;
        this.sub_image = sub_image;

    }



    public double getCost() {
        return cost;
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

    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setSub_image(String sub_image) {
        this.sub_image = sub_image;
    }
}
