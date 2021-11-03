package com.mobiledevteam.kuiki.Adapter;

import com.google.android.gms.maps.model.LatLng;

public class Taxi {
    private LatLng mTaxiLocation;
    private String mTaxiUid;
    private String mType;

    public Taxi(LatLng taxiLocation, String taxiUid, String type){
        mTaxiLocation=taxiLocation;
        mTaxiUid = taxiUid;
        mType = type;
    }

    public LatLng getmTaxiLocation() {
        return mTaxiLocation;
    }
    public String getmTaxiUid() {
        return mTaxiUid;
    }
    public String getmType() {
        return mType;
    }
}
