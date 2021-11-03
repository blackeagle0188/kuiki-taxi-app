package com.mobiledevteam.kuiki;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;
import com.mobiledevteam.kuiki.Adapter.DeliveryOffer;
import com.mobiledevteam.kuiki.Adapter.DriverOffer;
import com.mobiledevteam.kuiki.Adapter.DeliveryRequest;
import com.mobiledevteam.kuiki.data.RestaurantData;
import com.mobiledevteam.kuiki.data.ServicePaymentData;
import com.mobiledevteam.kuiki.data.SupermarketData;
import com.stripe.android.model.PaymentMethodCreateParams;

import java.util.ArrayList;

public class Common {
    private String baseURL = "https://dashboard.kuiki.mx/";
//    private String baseURL = "http://10.0.2.2/";
    private ArrayList<LatLng> locations;
    private String phonenumber;
    private String uid;
    private String jon_status;
    private int pay_amount;
    private Double distance;
    private String phone_token;
    private String user_phone_token;
    private String user_uid;
    private String driver_uid;
    private String pay_type;
    private String start_address;
    private String end_address;
    private String pay_status;
    private String ride_uuid;
    private PaymentMethodCreateParams params;
    private String paymentIntentClientSecret;
    private String avatar_link;
    private String user_name;
    private String comment;
    private int offertype;
    private boolean join;
    private DriverOffer accepted_offer;
    private DeliveryOffer deliveryRequest;
    private int option;
    private String mBrand;
    private Bitmap mBitmap;
    private String moto_type;

    private String mReview;

    private int mServiceType;
    private ServicePaymentData mServicePayment;
    private RestaurantData mRestaurant;
    private SupermarketData mSupermarket;
    private ArrayList<DeliveryRequest> deliveryRequests;

    private static Common instance = new Common();

    public static Common getInstance() {return instance;}

    public String getBaseURL() {return baseURL;}

    public ArrayList<LatLng> getLocations() {return locations;}

    public void setLocations(ArrayList<LatLng> location) {this.locations = location;}

    public String getPhonenumber() {return phonenumber;}

    public void setPhonenumber(String phonenumber) {this.phonenumber = phonenumber;}

    public String getUid() {return uid;}

    public void setUid(String _uid) {this.uid = _uid;}

    public String getJon_status() {return jon_status;}

    public void setJon_status(String jon_status) {this.jon_status = jon_status;}

    public int getPay_amount() {return pay_amount;}

    public void setPay_amount(int pay_amount) { this.pay_amount = pay_amount;}

    public Double getDistance() {return distance;}

    public void setDistance(Double distance) { this.distance = distance;}

    public String getPhone_token() { return phone_token;}

    public void setPhone_token(String phone_token) { this.phone_token = phone_token;}

    public String getUserPhone_token() { return user_phone_token;}

    public void setUserPhone_token(String phone_token) { this.user_phone_token = phone_token;}

    public String getUser_uid() { return user_uid;}

    public void setUser_uid(String uid) { this.user_uid = uid;}

    public String getDriver_uid() { return driver_uid;}

    public void setDriver_uid(String driver_uid) { this.driver_uid = driver_uid;}

    public String getPay_type() { return pay_type;}

    public void setPay_type(String pay_type) { this.pay_type = pay_type; }

    public String getStart_address() { return start_address; }

    public void setStart_address(String start_address) { this.start_address = start_address; }

    public void setEnd_address(String end_address) { this.end_address = end_address; }

    public String getEnd_address() { return end_address; }

    public String getPay_status() {return pay_status; }

    public void setPay_status(String pay_status) { this.pay_status = pay_status; }

    public String getRide_uuid() { return ride_uuid; }

    public void setRide_uuid(String ride_uuid) { this.ride_uuid = ride_uuid; }

    public PaymentMethodCreateParams getParams() {return params;}

    public void setParams(PaymentMethodCreateParams params) {this.params = params;}

    public String getPaymentIntentClientSecret() { return paymentIntentClientSecret;}

    public void setPaymentIntentClientSecret(String paymentIntentClientSecret) {this.paymentIntentClientSecret = paymentIntentClientSecret;}

    public String getAvatar() { return avatar_link;}

    public void setAvatar(String avatar) { this.avatar_link = avatar; }

    public String getUserName() { return user_name;}

    public void setUserName(String name) { this.user_name = name; }

    public int getOfferType() { return offertype;}

    public void setOfferType(int type) { this.offertype = type; }

    public boolean getJoin() { return join;}

    public void setJoin(boolean status) { this.join = status; }

    public String getComment() {return comment;}

    public void setComment(String str) {this.comment = str;}

    public DriverOffer getAcceptedOffer() {return accepted_offer;}

    public void setAcceptedOffer(DriverOffer offer) {this.accepted_offer = offer;}

    public DeliveryOffer getDeliveryRequest() {return deliveryRequest;}

    public void setDeliveryRequest(DeliveryOffer offer) {this.deliveryRequest = offer;}

    public int getOption() {return option;}

    public void setOption(int offer) {this.option = offer;}

    public String getBrand() {return mBrand;}

    public void setBrand(String brand) {this.mBrand = brand;}

    public String getReview() {return mReview;}

    public void setReview(String review) {this.mReview = review;}

    public Bitmap getBitmap() {return mBitmap;}

    public void setBitmap(Bitmap bitmap) {this.mBitmap = bitmap;}

    public String getMotoType() {return moto_type;}

    public void setMotoType(String type) {this.moto_type = type;}

    public ServicePaymentData getServicePayment() {return mServicePayment;}

    public void setServicePayment(ServicePaymentData servicePayment) {this.mServicePayment = servicePayment;}

    public RestaurantData getRestaurant() {return mRestaurant;}

    public void setRestaurant(RestaurantData restaurant) {this.mRestaurant = restaurant;}

    public SupermarketData getSupermarket() {return mSupermarket;}

    public void setSupermarket(SupermarketData supermarket) {this.mSupermarket = supermarket;}

    public int getServiceType() {return mServiceType;}

    public void setServiceType(int serviceType) {this.mServiceType = serviceType;}

    public ArrayList<DeliveryRequest> getDeliveryRequests() {return deliveryRequests;}

    public void setDeliveryRequests(ArrayList<DeliveryRequest> offer) {this.deliveryRequests = offer;}
}

