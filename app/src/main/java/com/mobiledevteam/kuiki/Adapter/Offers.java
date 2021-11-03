package com.mobiledevteam.kuiki.Adapter;

import android.media.Image;

import com.mobiledevteam.kuiki.Common;

public class Offers {
    private String mId = "";
    private String mAvatar = "";
    private String mUserUid = "";
    private String mName = "";
    private String mStartLocation = "";
    private String mEndLocation = "";
    private String mPrice = "";
    private String mDistance = "";
    private String mComment = "";
    private String mReview = "";

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getUid() {
        return mUserUid;
    }

    public void setUid(String uid) {
        mUserUid = uid;
    }

    public String getName() {return mName;}

    public void setName(String name) {
        mName = name;
    }

    public String getStartLocation() {return mStartLocation;}

    public void setStartLocation(String location) {
        mStartLocation = location;
    }

    public String getEndLocation() {return mEndLocation;}

    public void setEndLocation(String location) {
        mEndLocation = location;
    }

    public String getPrice() {return mPrice;}

    public void setPrice(String price) {
        mPrice = price;
    }

    public String getComment() {return mComment;}

    public void setComment(String comment) {
        mComment = comment;
    }

    public String getReview() {return mReview;}

    public void setReview(String review) {
        mReview = review;
    }

    public String getmImage() {
        return Common.getInstance().getBaseURL()+"/backend/" + mAvatar;
//        return mImage;
    }

    public void setImage(String avatar) {
        mAvatar = avatar;
    }
    public String getDistance() {
        return mDistance + " km";
    }

    public void setDistance(String distance) {
        mDistance = distance;
    }

    public boolean addOffer() {
        if( mId != "" && mAvatar != "" && mUserUid != "" && mName != "" && mStartLocation != "" && mEndLocation != "" && mPrice != "" && mDistance != "" ) {
            return true;
        } else {
         return  false;
        }
    }
}