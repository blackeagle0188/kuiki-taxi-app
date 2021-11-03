package com.mobiledevteam.kuiki.Adapter;

import com.mobiledevteam.kuiki.Common;

public class TripHistory {
    private String mDate = "";
    private String mStart = "";
    private String mEnd = "";
    private String mPrice = "";
    private int mStatus = 0;
    private String mStatusTxt = "";
    private int mType = 0;

    public String getDate() {return mDate;}

    public void setDate(String date) {
        mDate = date;
    }

    public String getStart() {return mStart;}

    public void setStart(String start) {
        mStart = start;
    }

    public String getEnd() {return mEnd;}

    public void setEnd(String end) {
        mEnd = end;
    }

    public String getPrice() {return mPrice;}

    public void setPrice(String price) {
        mPrice = price;
    }

    public int getStatus() {return mStatus;}

    public void setStatus(int status) { mStatus = status; }

    public String getStatusTxt() {return mStatusTxt;}

    public void setStatusTxt(String txt) {
        mStatusTxt = txt;
    }

    public int getType() {return mType;}

    public void setType(int type) {
        mType = type;
    }
}