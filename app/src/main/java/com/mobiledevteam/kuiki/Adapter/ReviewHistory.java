package com.mobiledevteam.kuiki.Adapter;

import android.widget.RatingBar;

public class ReviewHistory {
    private String mDate = "";
    private String mComment = "";
    private float mStar = 0;

    public String getDate() {return mDate;}

    public void setDate(String date) {
        mDate = date;
    }

    public String getComment() {return mComment;}

    public void setComment(String comment) {
        mComment = comment;
    }

    public float getStar() {return mStar;}

    public void setStar(float star) {
        mStar = star;
    }
}