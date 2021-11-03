package com.mobiledevteam.kuiki.data;

public class SupermarketData {
    private String strSupermarketName;
    private String strOrders;
    private String posPickUp;
    private String posDelivery;

    public String getSupermarketname() {
        return strSupermarketName;
    }

    public void setSupermarketname(String supermarket) {
        strSupermarketName = supermarket;
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
