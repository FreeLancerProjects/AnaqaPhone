package com.anaqaphone.models;

import java.io.Serializable;
import java.util.List;

public class OrderModel implements Serializable {

    private int id;
    private String user_id;
    private String address;
    private String address_lat;
    private String address_long;
    private String delivery_date;
    private String delivery_time;
    private String payment_type;
    private String total_cost;
    private String order_status;

    private List<OrdersDetails> order_product;

    public int getId() {
        return id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getAddress() {
        return address;
    }

    public String getAddress_lat() {
        return address_lat;
    }

    public String getAddress_long() {
        return address_long;
    }

    public String getDelivery_date() {
        return delivery_date;
    }

    public String getDelivery_time() {
        return delivery_time;
    }

    public String getPayment_type() {
        return payment_type;
    }

    public String getTotal_cost() {
        return total_cost;
    }

    public String getOrder_status() {
        return order_status;
    }

    public List<OrdersDetails> getOrder_product() {
        return order_product;
    }

    public class OrdersDetails implements Serializable {
        private Item item;
        private String id;
        private String order_id;
        private String item_id;
        private String amount;
        private String cost;
        private ProductInfo product_info;

        public Item getItem() {
            return item;
        }

        public String getId() {
            return id;
        }

        public String getOrder_id() {
            return order_id;
        }

        public String getItem_id() {
            return item_id;
        }

        public String getAmount() {
            return amount;
        }

        public String getCost() {
            return cost;
        }

        public ProductInfo getProduct_info() {
            return product_info;
        }

        public  class ProductInfo implements Serializable{
            private int id;
            private String title;
            private String image;
            private String price;

            public int getId() {
                return id;
            }

            public String getTitle() {
                return title;
            }

            public String getImage() {
                return image;
            }

            public String getPrice() {
                return price;
            }
        }

        public class Item implements Serializable {
            private String id;
            private String sub_image;
            private String price;
            private String dimensions;
            private String product_title;
            private String content;
            private String is_favourite;
            private String available;

            public String getId() {
                return id;
            }

            public String getSub_image() {
                return sub_image;
            }

            public String getPrice() {
                return price;
            }

            public String getDimensions() {
                return dimensions;
            }

            public String getProduct_title() {
                return product_title;
            }

            public String getContent() {
                return content;
            }

            public String getIs_favourite() {
                return is_favourite;
            }

            public String getAvailable() {
                return available;
            }
        }
    }




}
