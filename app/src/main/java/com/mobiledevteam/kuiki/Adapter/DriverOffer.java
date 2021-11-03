package com.mobiledevteam.kuiki.Adapter;

import android.media.Image;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.mobiledevteam.kuiki.Common;
import com.mobiledevteam.kuiki.R;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.sql.Driver;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class DriverOffer {
    FirebaseAuth mAuth;
    private String mId = "";
    private String mRideId = "";
    private String mAvatar = "";
    private String mDriverUid = "";
    private String mName = "";
    private String mPrice = "";
    private String mTime = "";
    private String mPlate = "";
    private String mColor = "";
    private String mDistance = "";
    private String mDriverPhone = "";
    private String mPhoneToken = "";
    private String mBrand = "";
    private String mReview = "";

    private OkHttpClient httpClient = new OkHttpClient();

    public String getId() {
        return mId;
    }

    public void setId(String mid) {
        mId = mid;
    }

    public String getUid() {
        return mDriverUid;
    }

    public void setUid(String uid) {
        mDriverUid = uid;
    }

    public String getRideid() {
        return mRideId;
    }

    public void setRideid(String ride_id) {
        mRideId = ride_id;
    }

    public String getName() {return mName;}

    public void setName(String name) {
        mName = name;
    }

    public String getPrice() {return mPrice;}

    public void setPrice(String price) {
        mPrice = price;
    }

    public String getTime() {return mTime;}

    public void setTime(String time) {
        mTime = time;
    }

    public String getPlate() {return mPlate;}

    public void setPlate(String plate) {
        mPlate = plate;
    }

    public String getColor() {return mColor;}

    public void setColor(String color) {
        mColor = color;
    }

    public String getmImage() {
        return Common.getInstance().getBaseURL()+"/backend/" + mAvatar;
//        return mImage;
    }

    public void setBrand(String brand) {
        mBrand = brand;
    }

    public String getBrand() {return mBrand;}

    public String getReview() {return mReview;}

    public void setReview(String review) {
        mReview = review;
    }

    public void setImage(String avatar) {
        mAvatar = avatar;
    }

    public String getDistance() {
        Float dis = Float.parseFloat(mDistance);
        if(dis > 1000) {
            float km_dis = dis / 1000;
            return String.format("%.2f", km_dis) + " km";
        } else {
            return mDistance + " m";
        }
    }

    public void setDistance(String distance) {
        mDistance = distance;
    }

    public boolean addOffer() {
        if( mId != "" && mAvatar != "" && mDriverUid != "" && mName != "" && mPrice != "" && mDistance != "" && mDistance != "" ) {
            return true;
        } else {
         return  false;
        }
    }

    public String getPhone() {return mDriverPhone;}

    public void setPhone(String phone) {
        mDriverPhone = phone;
    }

    public String getPhoneToken() {return mPhoneToken;}

    public void setPhoneToken(String phone) {
        mPhoneToken = phone;
    }

    public void setProgressValue(final int progress, ProgressBar timer) {

        // set the progress
        timer.setProgress(progress);
        int val = timer.getProgress();
        // thread is used to change the progress value
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep( 200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(val == 0) {
                    Thread.currentThread().interrupt();
                } else {
                    setProgressValue(progress - 1, timer);
                }
            }
        });
        thread.start();
    }

    public void accept(Button btn, FirebaseDatabase mDatabase, FirebaseAuth mAuth) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.getReference("user/"+mAuth.getUid()+"/join").setValue("off");
                mDatabase.getReference("ride/"+getRideid()+"/status").setValue("accept");
                mDatabase.getReference("ride/"+getRideid()+"/drivernumber").setValue(getPhone());
                mDatabase.getReference("ride/"+getRideid()+"/driver").setValue(getUid());
                Common.getInstance().setDriver_uid(getUid());
                Common.getInstance().setAcceptedOffer(DriverOffer.this);
                Common.getInstance().setPhone_token(mPhoneToken);
                mDatabase.getReference("offer/"+getId()+"/status").setValue("accepted");
                Thread.currentThread().interrupt();
            }
        });
    }

    public void reject(Button btn, FirebaseDatabase mDatabase) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.getReference("offer/"+getId()).removeValue();
            }
        });
    }
}