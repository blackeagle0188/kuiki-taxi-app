package com.mobiledevteam.kuiki.data;

import com.google.android.gms.maps.model.LatLng;

public class ServicePaymentData {
    private String strServiceCompany;
    private String strServiceNumber;
    private int mPrice;
    private String posPickUp;
    private String posDelivery;

    public String getServiceCompany() {
        return strServiceCompany;
    }

    public void setServiceCompany(String serviceCompany) {
        strServiceCompany = serviceCompany;
    }

    public String getServiceNumber() {
        return strServiceNumber;
    }

    public void setServiceNumber(String serviceNumber) {
        strServiceNumber = serviceNumber;
    }

    public int getPrice() {
        return mPrice;
    }

    public void setPrice(int price) {
        mPrice = price;
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
