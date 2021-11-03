package com.mobiledevteam.kuiki.data;

public class RestaurantData {
    private String strRestaurantName;
    private String strOrders;
    private String posPickUp;
    private String posDelivery;

    public String getRestaurantname() {
        return strRestaurantName;
    }

    public void setRestaurantname(String restaurant) {
        strRestaurantName = restaurant;
    }

    public String getOrder() {
        return strOrders;
    }

    public void setOrder(String order) {
        strOrders = order;
    }

    public String getPickUp() {
        return posPickUp;
    }

    public void setPickUp(String pickup) {
        posPickUp = pickup;
    }

    public String getDelivery() {
        return posDelivery;
    }

    public void setDelivery(String delivery) {
        posDelivery = delivery;
    }
}
