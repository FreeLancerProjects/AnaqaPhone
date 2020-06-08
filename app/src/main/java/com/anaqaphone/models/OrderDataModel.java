package com.anaqaphone.models;

import java.io.Serializable;
import java.util.List;

public class OrderDataModel implements Serializable {

    private Meta meta;
    private List<OrderModel> data;


    public Meta getMeta() {
        return meta;
    }

    public List<OrderModel> getData() {
        return data;
    }


    public static class Meta {

        private int current_page;
        private int last_page;

        public int getCurrent_page() {
            return current_page;
        }

        public int getLast_page() {
            return last_page;
        }
    }
}
