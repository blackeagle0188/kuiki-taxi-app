package com.mobiledevteam.kuiki.Adapter;

import com.google.android.gms.maps.model.LatLng;
import com.mobiledevteam.kuiki.data.RestaurantData;
import com.mobiledevteam.kuiki.data.ServicePaymentData;
import com.mobiledevteam.kuiki.data.SupermarketData;

public class DeliveryRequest {
    private String mId = "";
    private String user_id = "";
    private String user_name  = "";
    private String user_avatar = "";
    private String user_phone = "";
    private int delivery_type = 0;
    private LatLng user_location;
    private String user_address = "";
    private String review = "";
    private ServicePaymentData servicePayment;
    private RestaurantData restaurant;
    private SupermarketData supermarket;

    public String getId() {
        return mId;
    }

    public void setId(String id) {mId = id;}

    public String getUserId() {
        return user_id;
    }

    public void setUserId(String id) {user_id = id;}

    public String getUserName() {
        return user_name;
    }

    public void setUserName(String name) {user_name = name;}

    public String getUserAvatar() {
        return user_avatar;
    }

    public void setUserAvatar(String avatar) {user_avatar = avatar;}

    public String getUserPhone() {
        return user_phone;
    }

    public void setUserPhone(String phone) {user_phone = phone;}

    public int getType() {
        return delivery_type;
    }

    public void setType(int type) {delivery_type = type;}

    public LatLng getUserLocation() {
        return user_location;
    }

    public void setUserLocation(LatLng location) {user_location = location;}

    public String getUserAddress() {
        return user_address;
    }

    public void setUserAddress(String address) {user_address = address;}

    public String getReview() {
        return review;
    }

    public void setReview(String data) {review = data;}

    public ServicePaymentData getServicePayment() { return servicePayment; }

    public void setServicePayment( ServicePaymentData data ) { servicePayment = data; }

    public RestaurantData getRestaurant() { return restaurant; }

    public void setRestaurant( RestaurantData data ) { restaurant = data; }

    public SupermarketData getSupermarket() { return supermarket; }

    public void setSupermarket( SupermarketData data ) { supermarket = data; }
}
