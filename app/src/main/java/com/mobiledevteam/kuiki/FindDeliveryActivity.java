package com.mobiledevteam.kuiki;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.mobiledevteam.kuiki.Adapter.DeliveryOffer;
import com.mobiledevteam.kuiki.Adapter.DeliveryOfferAdapter;
import com.mobiledevteam.kuiki.Adapter.PageViewAdapter;
import com.mobiledevteam.kuiki.data.RestaurantData;
import com.mobiledevteam.kuiki.data.ServicePaymentData;
import com.mobiledevteam.kuiki.data.SupermarketData;

import java.util.ArrayList;
import java.util.UUID;

import static java.util.Locale.getDefault;

public class FindDeliveryActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mOfferRef;
    private DatabaseReference mDeliveryCheckRef;
    private DatabaseReference mUserRef;
    private Geocoder geocoder;
    private SupportMapFragment mapFragment;
    private LocationManager locationManager;
    private String uniqueId;

    private TextView btnCancelTrip;
    private TextView tvServiceType;
    private TextView tvPickUpPos;
    private TextView tvDeliveryPos;
    private RelativeLayout layoutGrid;
    private GridView offerGrid;
    private TextView btnCancel;
    private ViewPager _clinicSlider;

    private int serviceType;
    private boolean isNew = false;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mDatabase.getReference("delivery/" + uniqueId).removeValue();
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private ServicePaymentData servicePayment;
    private RestaurantData restaurant;
    private SupermarketData supermarket;
    private LatLng my_location;
    private ArrayList<LatLng> locations;
    private ArrayList<DeliveryOffer> mAllEventList = new ArrayList<>();

    private DeliveryOfferAdapter adapter_event;

    private int slideCurrentItem=0;
    private ArrayList<String> mSliderImages = new ArrayList<>();

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d("selected:", String.valueOf(slideCurrentItem));
            _clinicSlider.setCurrentItem(slideCurrentItem);
            slideCurrentItem = slideCurrentItem + 1;
            if (mSliderImages.size()>slideCurrentItem){

            }else{
                slideCurrentItem = 0;
            }
            timerHandler.postDelayed(this, 5000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_delivery);

        Intent intent = getIntent();
        String status = intent.getStringExtra("create");
        if(status.equals("yes")) {
            isNew = true;
            uniqueId = UUID.randomUUID().toString();
        } else {
            isNew = false;
            uniqueId = status;
        }

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        geocoder = new Geocoder(this, getDefault());


        serviceType = Common.getInstance().getServiceType();
        locations = new ArrayList<LatLng>();

        btnCancelTrip = (TextView) findViewById(R.id.btn_delivery_cancel);
        tvServiceType = (TextView) findViewById(R.id.delivery_type);
        tvPickUpPos = (TextView) findViewById(R.id.delivery_start);
        tvDeliveryPos = (TextView) findViewById(R.id.delivery_end);
        layoutGrid = (RelativeLayout) findViewById(R.id.layout_offer_grid);
        offerGrid = (GridView) findViewById(R.id.grid_delivery_offer);
        btnCancel = (TextView) findViewById(R.id.btn_delivery_cancel);
        _clinicSlider = (ViewPager) findViewById(R.id.slider_clinic);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        my_location = new LatLng(48.85299, 2.34288);
        Location LocationGps;
        String provider = locationManager.getBestProvider(new Criteria(), true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        } else {
            LocationGps = locationManager.getLastKnownLocation(provider);
        }
        if (LocationGps != null) {
            my_location = new LatLng(LocationGps.getLatitude(), LocationGps.getLongitude());
            mDatabase.getReference("user/" + mAuth.getUid() + "/latitude").setValue(my_location.latitude);
            mDatabase.getReference("user/" + mAuth.getUid() + "/longitude").setValue(my_location.longitude);

        } else {
            my_location = new LatLng(48.85299, 2.34288);
        }

        switch (serviceType) {
            case 0:
                servicePayment = Common.getInstance().getServicePayment();
                tvServiceType.setText(getResources().getString(R.string.string_service_payment));
                break;
            case 1:
                restaurant = Common.getInstance().getRestaurant();
                tvServiceType.setText(getResources().getString(R.string.string_restaurant));
                break;
            case 2:
                supermarket = Common.getInstance().getSupermarket();
                tvServiceType.setText(getResources().getString(R.string.string_supermarket));
                break;
            case 3:
                break;
        }

        onSetView();
        if(isNew) {
            onCreateNewOffer();
        }
        getData();
        getSliderData();
        onCheckDelivery();
    }

    private void onCreateNewOffer() {
        mDatabase.getReference("delivery/" + uniqueId + "/delivery_type").setValue(serviceType);
        if(serviceType == 0) {
            mDatabase.getReference("delivery/" + uniqueId + "/service_company").setValue(servicePayment.getServiceCompany());
            mDatabase.getReference("delivery/" + uniqueId + "/service_number").setValue(servicePayment.getServiceNumber());
            mDatabase.getReference("delivery/" + uniqueId + "/price").setValue(servicePayment.getPrice());
            mDatabase.getReference("delivery/" + uniqueId + "/pickup_pos").setValue(servicePayment.getPickUp());
            mDatabase.getReference("delivery/" + uniqueId + "/delivery_pos").setValue(servicePayment.getDelivery());
        } else if (serviceType == 1) {
            mDatabase.getReference("delivery/" + uniqueId + "/restaurant").setValue(restaurant.getRestaurantname());
            mDatabase.getReference("delivery/" + uniqueId + "/orders").setValue(restaurant.getOrder());
            mDatabase.getReference("delivery/" + uniqueId + "/pickup_pos").setValue(restaurant.getPickUp());
            mDatabase.getReference("delivery/" + uniqueId + "/delivery_pos").setValue(restaurant.getDelivery());
        } else if(serviceType == 2) {
            mDatabase.getReference("delivery/" + uniqueId + "/supermarket").setValue(supermarket.getSupermarketname());
            mDatabase.getReference("delivery/" + uniqueId + "/orders").setValue(supermarket.getOrder());
            mDatabase.getReference("delivery/" + uniqueId + "/pickup_pos").setValue(supermarket.getPickUp());
            mDatabase.getReference("delivery/" + uniqueId + "/delivery_pos").setValue(supermarket.getDelivery());
        }

        mDatabase.getReference("delivery/" + uniqueId + "/user").setValue(mAuth.getUid());
        mDatabase.getReference("delivery/" + uniqueId + "/user_name").setValue(Common.getInstance().getUserName());
        mDatabase.getReference("delivery/" + uniqueId + "/avatar").setValue(Common.getInstance().getAvatar());
        mDatabase.getReference("delivery/" + uniqueId + "/phone").setValue(Common.getInstance().getPhonenumber());
        mDatabase.getReference("delivery/" + uniqueId + "/review").setValue(Common.getInstance().getReview());
        mDatabase.getReference("delivery/" + uniqueId + "/status").setValue("waiting");
    }

    private void getSliderData(){

//        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppTheme_Bright_Dialog);
//        progressDialog.setIndeterminate(true);
//        progressDialog.setMessage("Authenticating...");
//        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        progressDialog.setCancelable(false);
//        progressDialog.show();
        JsonObject json = new JsonObject();

        try {
            Ion.with(this)
                    .load(Common.getInstance().getBaseURL()+"api/getSliderImage")
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
//                            progressDialog.dismiss();
                            Log.d("result::", result.toString());
                            if (result != null) {
                                JsonArray clinics_images = result.get("data").getAsJsonArray();

                                for(JsonElement imageElement : clinics_images){
                                    JsonObject theimage = imageElement.getAsJsonObject();
                                    String image_id = theimage.get("id").getAsString();
                                    String image_url = theimage.get("url").getAsString();
                                    mSliderImages.add(Common.getInstance().getBaseURL() + "backend/" + image_url);
                                }
                                initSlider();
                            } else {

                            }
                        }
                    });
        }catch(Exception e){
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void onSetView() {
        if(serviceType == 0) {
            tvPickUpPos.setText(servicePayment.getPickUp());
            tvDeliveryPos.setText(servicePayment.getDelivery());
        } else if(serviceType == 1) {
            tvPickUpPos.setText(restaurant.getPickUp());
            tvDeliveryPos.setText(restaurant.getDelivery());
        } else if (serviceType == 2) {
            tvPickUpPos.setText(supermarket.getPickUp());
            tvDeliveryPos.setText(supermarket.getDelivery());
        }

        _clinicSlider.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.d("selecteditem:", String.valueOf(position));
            }
            @Override
            public void onPageSelected(int position) {
                Log.d("selectedpoist:", String.valueOf(position));
                slideCurrentItem = position + 1;
                if (mSliderImages.size()>slideCurrentItem){

                }else{
                    slideCurrentItem = 0;
                }
                timerHandler.removeCallbacks(timerRunnable);
                timerHandler.postDelayed(timerRunnable, 5000);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        btnCancelTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.getReference("delivery/" + uniqueId).removeValue();
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void onCheckDelivery() {
        mDeliveryCheckRef = mDatabase.getReference("delivery/" + uniqueId + "/status");
        mDeliveryCheckRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String statusRide = dataSnapshot.getValue(String.class);
                if (statusRide != null) {
                    if (statusRide.equals("accept")) {
                        Intent intent = new Intent(getApplicationContext(), UserDeliveryActivity.class);
                        intent.putExtra("create", "yes");
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getData() {
        mOfferRef = mDatabase.getReference("deliveryoffer/");
        mOfferRef.orderByChild("ride").equalTo(uniqueId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot ds, @Nullable String s) {
                getTripLists(ds);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot ds, @Nullable String previousChildName) {
                getTripLists(ds);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String ride_uuid = snapshot.getKey();
                for(int i = 0; i < mAllEventList.size(); i++) {
                    DeliveryOffer offer = mAllEventList.get(i);
                    if(offer.getId().equals(ride_uuid)) {
                        mAllEventList.remove(i);
                    }
                }
                initView(false);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void getTripLists(DataSnapshot ds) {
        String ride_id = ds.child("ride").getValue(String.class);
        String driver_id = ds.child("driver").getValue(String.class);
        String driver_name = ds.child("driver_name").getValue(String.class);
        String driver_avatar = ds.child("driver_avatar").getValue(String.class);
        String driver_brand = ds.child("driver_brand").getValue(String.class);
        String driver_price = ds.child("price").getValue(String.class);
        String driver_time = ds.child("time").getValue(String.class);
        String driver_phone = ds.child("driver_phone").getValue(String.class);
        String status = ds.child("status").getValue(String.class);
        String driverphone = ds.child("driverphone").getValue(String.class);
        String review = ds.child("review").getValue(String.class);
        if(ride_id != null && driver_id != null && driver_name != null && driver_avatar != null && driver_brand != null && driver_price != null && driver_time!= null && driverphone != null && status != null && driver_phone != null && review != null) {
            String mId = ds.getKey();

            DeliveryOffer offer = new DeliveryOffer();
            for(DeliveryOffer item : mAllEventList) {
                if(item.getId().equals(mId)) {
                    return;
                }
            }

            if(driver_name.split("\\w+").length>1){

                String lastName = driver_name.substring(driver_name.lastIndexOf(" ")+1);
                String firstName = driver_name.substring(0, driver_name.lastIndexOf(' '));
                lastName = lastName.substring(0, 1) + ".";
                driver_name = firstName + " " + lastName;
            }
            offer.setId(mId);
            offer.setUid(driver_id);
            offer.setRideid(ride_id);
            offer.setName(driver_name);
            offer.setImage(driver_avatar);
            offer.setBrand(driver_brand);
            offer.setPhoneToken(driver_phone);
            offer.setPrice(String.valueOf(driver_price));
            offer.setTime(String.valueOf(driver_time));
            offer.setPhone(driverphone);
            offer.setReview(review);

            mAllEventList.add(offer);

            initView(true);
        }
    }

    private void initView(boolean added){
        if(added) {
            Vibrator v = (Vibrator) getSystemService(getApplicationContext().VIBRATOR_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                v.vibrate(500);
            }
        }
        adapter_event = new DeliveryOfferAdapter(getBaseContext(), mAllEventList);
        if(mAllEventList.size() > 0) {
            layoutGrid.setVisibility(View.VISIBLE);
        } else {
            layoutGrid.setVisibility(View.GONE);
        }
        offerGrid.setAdapter(adapter_event);
    }

    private void viewExistTrip() {
        int offer_type = Common.getInstance().getOfferType();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.getReference("delivery/" + uniqueId).removeValue();
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mDeliveryCheckRef = mDatabase.getReference("ride/" + uniqueId + "/status");
        mDeliveryCheckRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String statusRide = dataSnapshot.getValue(String.class);
                if (statusRide != null) {
                    if (statusRide.equals("accept")) {
                        Intent intent = new Intent(getApplicationContext(), UserDeliveryActivity.class);
                        intent.putExtra("create", "yes");
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initSlider() {
        PageViewAdapter adapter = new PageViewAdapter(this, mSliderImages);
        _clinicSlider.setAdapter(adapter);
    }
}