package com.anaqaphone.models;

import java.io.Serializable;
import java.util.List;

public class FavouriteDataModel implements Serializable {
    private List<FavouriteData> data;

    public List<FavouriteData> getData() {
        return data;
    }

    public class FavouriteData implements Serializable {
        private SingleProductDataModel product;
        private User user;

        public SingleProductDataModel getProduct() {
            return product;
        }

        public User getUser() {
            return user;
        }

        public class User implements Serializable {
            private int id;
            private String name;
            private String email;
            private String user_type;
            private String phone_code;
            private String phone;
            private String logo;
            private String block;
            private String login;
            private String latitude;
            private String longitude;
            private String address;
            private long logout_time;
            private String token;

            public int getId() {
                return id;
            }

            public String getName() {
                return name;
            }

            public String getEmail() {
                return email;
            }

            public String getUser_type() {
                return user_type;
            }

            public String getPhone_code() {
                return phone_code;
            }

            public String getPhone() {
                return phone;
            }

            public String getLogo() {
                return logo;
            }

            public String getBlock() {
                return block;
            }

            public String getLogin() {
                return login;
            }

            public String getLatitude() {
                return latitude;
            }

            public String getLongitude() {
                return longitude;
            }

            public String getAddress() {
                return address;
            }

            public long getLogout_time() {
                return logout_time;
            }

            public String getToken() {
                return token;
            }
        }
    }
}
