package com.mobiledevteam.kuiki.Adapter;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.mobiledevteam.kuiki.Common;

import okhttp3.OkHttpClient;

public class DeliveryOffer {
    FirebaseAuth mAuth;
    private String mId = "";
    private String mRideId = "";
    private String mAvatar = "";
    private String mDriverUid = "";
    private String mName = "";
    private String mPrice = "";
    private String mTime = "";
    private String mPlate = "";
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

    public String getmImage() {
        return Common.getInstance().getBaseURL()+"/backend/" + mAvatar;
//        return mImage;
    }

    public void setBrand(String brand) {
        mBrand = brand;
    }

    public String getBrand() {return mBrand;}

    public void setImage(String avatar) {
        mAvatar = avatar;
    }

    public boolean addOffer() {
        if( mId != "" && mAvatar != "" && mDriverUid != "" && mName != "" && mPrice != "") {
            return true;
        } else {
            return  false;
        }
    }

    public String getPhone() {return mDriverPhone;}

    public void setPhone(String phone) {
        mDriverPhone = phone;
    }

    public String getReview() {return mReview;}

    public void setReview(String review) {
        mReview = review;
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
                mDatabase.getReference("delivery/"+getRideid()+"/status").setValue("accept");
                mDatabase.getReference("delivery/"+getRideid()+"/drivernumber").setValue(getPhone());
                mDatabase.getReference("delivery/"+getRideid()+"/driver").setValue(getUid());
                mDatabase.getReference("delivery/"+getRideid()+"/price").setValue(Integer.parseInt(getPrice()));
                Common.getInstance().setDriver_uid(getUid());
                Common.getInstance().setDeliveryRequest(DeliveryOffer.this);
                Common.getInstance().setPhone_token(mPhoneToken);
                Common.getInstance().setPhonenumber(getPhone());
                mDatabase.getReference("deliveryoffer/"+getId()+"/status").setValue("accepted");
                Thread.currentThread().interrupt();
            }
        });
    }

    public void reject(Button btn, FirebaseDatabase mDatabase) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.getReference("deliveryoffer/"+getId()).removeValue();
            }
        });
    }
}
