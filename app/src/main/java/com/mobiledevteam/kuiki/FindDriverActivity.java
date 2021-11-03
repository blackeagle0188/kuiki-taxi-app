package com.mobiledevteam.kuiki;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
//import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.installations.FirebaseInstallations;
import com.mobiledevteam.kuiki.Adapter.DriverOffer;
import com.mobiledevteam.kuiki.Adapter.DriverOfferAdapter;
import com.mobiledevteam.kuiki.Adapter.Taxi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import okhttp3.OkHttpClient;

public class FindDriverActivity extends AppCompatActivity implements OnMapReadyCallback {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mdriverTokenRef;
    private DatabaseReference mdriverPhoneRef;
    private DatabaseReference mRideCheckRef;
    private DatabaseReference mUserRef;
    private DatabaseReference mOfferRef;
    private DatabaseReference mRef;

    private TextView mStartLocation;
    private TextView mEndLocation;
    private TextView mCancel;
    private SupportMapFragment mapFragment;
    private LinearLayout driver_offer;
    private Button btn_driver_offer;
    private GridView offer_grid;
    private RelativeLayout layout_offer_grid;
    private TextView txt_find_title;
    private Button btn_price_minus;
    private Button btn_price_plus;
    private TextView txt_changed_price;
    private Button btn_increase_price;
    private LinearLayout layout_price_change;

    private String start_addresses;
    private String end_addresses;
    private ArrayList<LatLng> locations;
    private Bitmap start_smallMarker;
    private Bitmap end_smallMarker;
    MarkerOptions start_markerOptions;
    MarkerOptions end_markerOptions;
    private String uniqueId;
    private String driverUid;
    private int height = 100;
    private int width = 70;

    private String userPhone = "no";
    private String drivertoken = "no";
    private String driverPhone = "no";
    private String pay_type;
    private int payPrice;
    private int changedPrice;
    private int offer_price;
    private Location mMyLocation;
    private Location mTaxiLocation;
    private LatLng myLocation;
    private Bitmap smallMarker;

    private OkHttpClient httpClient = new OkHttpClient();
    String currentDateandTime;

    private GoogleMap mMap;

    private ArrayList<DriverOffer> mAllEventList = new ArrayList<>();
    private ArrayList<Taxi> mTaxis = new ArrayList<>();
    DriverOfferAdapter adapter_event;

    String phone_token = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_driver);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        locations = Common.getInstance().getLocations();
        myLocation = locations.get(0);

        adapter_event = new DriverOfferAdapter(getBaseContext(), mAllEventList);

        mStartLocation = (TextView) findViewById(R.id.start_location);
        mEndLocation = (TextView) findViewById(R.id.end_location);
        mCancel = (TextView) findViewById(R.id.btn_cancel);
        driver_offer = (LinearLayout) findViewById(R.id.driver_offer);
        driver_offer.setVisibility(View.GONE);
        btn_driver_offer = (Button) findViewById(R.id.btn_driver_offer);
        offer_grid = (GridView) findViewById(R.id.grid_driver_offer);
        layout_offer_grid = (RelativeLayout) findViewById(R.id.layout_offer_grid);
        txt_find_title = (TextView) findViewById(R.id.txt_find_title);
        btn_price_minus = (Button) findViewById(R.id.btn_price_minus);
        btn_price_plus = (Button) findViewById(R.id.btn_price_plus);
        txt_changed_price = (TextView) findViewById(R.id.txt_changed_price);
        btn_increase_price = (Button) findViewById(R.id.btn_increase_price);
        layout_price_change = (LinearLayout) findViewById(R.id.layout_price_change);
        if(Common.getInstance().getOfferType() == 1) {
            layout_price_change.setVisibility(View.GONE);
        }
        btn_increase_price.setClickable(false);
        btn_driver_offer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.getReference("user/"+mAuth.getUid()+"/join").setValue("off");
                mDatabase.getReference("ride/"+uniqueId+"/status").setValue("accept");
            }
        });
        btn_price_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(changedPrice -5 < payPrice) {
                    return;
                }
                changedPrice -= 5;
                if(changedPrice == payPrice) {
                    btn_increase_price.setClickable(false);
                    btn_increase_price.setBackgroundResource(R.drawable.back_gray_button);
                    btn_increase_price.setTextColor(getResources().getColor(R.color.colorBlack));
                } else {
                    btn_increase_price.setClickable(true);
                    btn_increase_price.setBackgroundResource(R.drawable.back_pink_button);
                    btn_increase_price.setTextColor(getResources().getColor(R.color.colorWhite));
                }
                txt_changed_price.setText(String.valueOf(changedPrice) + "MXN");
            }
        });
        btn_price_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changedPrice += 5;
                if(changedPrice == payPrice) {
                    btn_increase_price.setClickable(false);
                    btn_increase_price.setBackgroundResource(R.drawable.back_gray_button);
                    btn_increase_price.setTextColor(getResources().getColor(R.color.colorBlack));
                } else {
                    btn_increase_price.setClickable(true);
                    btn_increase_price.setBackgroundResource(R.drawable.back_pink_button);
                    btn_increase_price.setTextColor(getResources().getColor(R.color.colorWhite));
                }
                txt_changed_price.setText(String.valueOf(changedPrice) + "MXN");
            }
        });

        btn_increase_price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.getReference("ride/" + uniqueId + "/price").setValue(changedPrice);
                payPrice = changedPrice;
                btn_increase_price.setClickable(false);
                btn_increase_price.setBackgroundResource(R.drawable.back_gray_button);
                btn_increase_price.setTextColor(getResources().getColor(R.color.colorBlack));
            }
        });

        BitmapDrawable bitmapdraw1 = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_map_start);
        Bitmap b1 = bitmapdraw1.getBitmap();
        start_markerOptions = new MarkerOptions();
        start_smallMarker = Bitmap.createScaledBitmap(b1, 120, 120, false);
        start_markerOptions.icon(BitmapDescriptorFactory.fromBitmap(start_smallMarker));
        BitmapDrawable bitmapdraw2 = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_map_end);
        Bitmap b2 = bitmapdraw2.getBitmap();
        end_markerOptions = new MarkerOptions();
        end_smallMarker = Bitmap.createScaledBitmap(b2, 120, 120, false);
        end_markerOptions.icon(BitmapDescriptorFactory.fromBitmap(end_smallMarker));

        BitmapDrawable bitmapDrawable = (BitmapDrawable)getResources().getDrawable(R.drawable.ic_car_front);
        Bitmap b = bitmapDrawable.getBitmap();
        smallMarker = Bitmap.createScaledBitmap(b, 100, 100, false);
        mMyLocation = new Location("User");
        mTaxiLocation = new Location("Taxi");

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        uniqueId = getIntent().getStringExtra("uuid");
        if(uniqueId.equals("null")) {
            startView();
        } else {
            Common.getInstance().setRide_uuid(uniqueId);
            viewExistTrip();
        }

        getData();

        showTaxi();
    }

    private void getData() {
        mOfferRef = mDatabase.getReference("offer/");
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
                    DriverOffer offer = mAllEventList.get(i);
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
        String car_color = ds.child("color").getValue(String.class);
        String driver_phone = ds.child("driver_phone").getValue(String.class);
        String distance = ds.child("distance").getValue(String.class);
        String status = ds.child("status").getValue(String.class);
        String driverphone = ds.child("driverphone").getValue(String.class);
        String review = ds.child("review").getValue(String.class);
        if(ride_id != null && driver_id != null && driver_name != null && driver_avatar != null && driver_brand != null && driver_price != null && driver_time!= null && distance != null && driverphone != null && status != null && driver_phone != null && review != null) {
            String mId = ds.getKey();

            DriverOffer offer = new DriverOffer();
            for(DriverOffer item : mAllEventList) {
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
            offer.setRideid(ride_id);
            offer.setUid(driver_id);
            offer.setName(driver_name);
            offer.setImage(driver_avatar);
            offer.setBrand(driver_brand);
            offer.setPhoneToken(driver_phone);
            offer.setPrice(String.valueOf(driver_price) + "MXN");
            offer.setTime(String.valueOf(driver_time) + " MIN");
            offer.setDistance(distance);
            offer.setColor(car_color);
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
        adapter_event = new DriverOfferAdapter(getBaseContext(), mAllEventList);
        if(mAllEventList.size() > 0) {
            layout_offer_grid.setVisibility(View.VISIBLE);
        } else {
            layout_offer_grid.setVisibility(View.GONE);
        }
        offer_grid.setAdapter(adapter_event);
    }

    private void viewExistTrip() {
        int offer_type = Common.getInstance().getOfferType();
        if(offer_type == 0) {
            txt_find_title.setText(getResources().getString(R.string.string_placing_offer));
        } else {
            txt_find_title.setText(getResources().getString(R.string.string_searching_driverr));
        }

        start_addresses = Common.getInstance().getStart_address();
        end_addresses = Common.getInstance().getEnd_address();
        pay_type = Common.getInstance().getPay_type();
        payPrice = Common.getInstance().getPay_amount();
        changedPrice = payPrice;

        drawRoute();
        txt_changed_price.setText(String.valueOf(changedPrice) + " MXN");
        mStartLocation.setText(start_addresses);
        mEndLocation.setText(end_addresses);

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.getReference("ride/" + uniqueId).removeValue();
                Intent intent = new Intent(FindDriverActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mUserRef = mDatabase.getReference("user/" + mAuth.getUid() + "/phonenumber");
        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userPhone = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mdriverTokenRef = mDatabase.getReference("user/" + driverUid + "/phonetoken");
        mdriverTokenRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                drivertoken = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mdriverPhoneRef = mDatabase.getReference("user/" + driverUid + "/phonenumber");
        mdriverPhoneRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                driverPhone = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mRideCheckRef = mDatabase.getReference("ride/" + uniqueId + "/status");
        mRideCheckRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String statusRide = dataSnapshot.getValue(String.class);
                if (statusRide != null) {
                    if (statusRide.equals("accept")) {
                        Intent intent = new Intent(FindDriverActivity.this, UserRideActivity.class);
                        intent.putExtra("uuid", "null");
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mRideCheckRef = mDatabase.getReference("ride/" + uniqueId + "/passenger_offer");
        mRideCheckRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    int passenger_offer = dataSnapshot.getValue(Integer.class);
                    driver_offer.setVisibility(View.VISIBLE);
                    btn_driver_offer.setText(getResources().getString(R.string.string_accept_mxn) + passenger_offer);
                } catch (Exception e) {
                    driver_offer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void startView() {
        int offer_type = Common.getInstance().getOfferType();
        if(offer_type == 0) {
            txt_find_title.setText(getResources().getString(R.string.string_placing_offer));
        } else {
            txt_find_title.setText(getResources().getString(R.string.string_searching_driverr));
        }

        start_addresses = Common.getInstance().getStart_address();
        end_addresses = Common.getInstance().getEnd_address();
        pay_type = Common.getInstance().getPay_type();
        payPrice = Common.getInstance().getPay_amount();
        changedPrice = payPrice;

        drawRoute();
        txt_changed_price.setText(String.valueOf(changedPrice) + " MXN");

        mStartLocation.setText(start_addresses);
        mEndLocation.setText(end_addresses);
        uniqueId = UUID.randomUUID().toString();
        driverUid = Common.getInstance().getDriver_uid();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
        currentDateandTime = sdf.format(new Date());
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.getReference("ride/" + uniqueId).removeValue();
                Intent intent = new Intent(FindDriverActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mUserRef = mDatabase.getReference("user/" + mAuth.getUid() + "/phonenumber");
        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userPhone = dataSnapshot.getValue(String.class);
                requestRide();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mdriverTokenRef = mDatabase.getReference("user/" + driverUid + "/phonetoken");
        mdriverTokenRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                drivertoken = dataSnapshot.getValue(String.class);
//                requestRide();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mdriverPhoneRef = mDatabase.getReference("user/" + driverUid + "/phonenumber");
        mdriverPhoneRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                driverPhone = dataSnapshot.getValue(String.class);
//                requestRide();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mRideCheckRef = mDatabase.getReference("ride/" + uniqueId + "/status");
        mRideCheckRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String statusRide = dataSnapshot.getValue(String.class);
                if (statusRide != null) {
                    if (statusRide.equals("accept")) {
                        Intent intent = new Intent(FindDriverActivity.this, UserRideActivity.class);
                        intent.putExtra("uuid", "null");
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mRideCheckRef = mDatabase.getReference("ride/" + uniqueId + "/passenger_offer");
        mRideCheckRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    int passenger_offer = dataSnapshot.getValue(Integer.class);
                    driver_offer.setVisibility(View.VISIBLE);
                    btn_driver_offer.setText(getResources().getString(R.string.string_accept_mxn) + passenger_offer);
                } catch (Exception e) {
                    driver_offer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showTaxi(){
        mRef = mDatabase.getReference("user/");
        mRef.orderByChild("type").equalTo("driver").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("datasa:", String.valueOf(dataSnapshot));
                mTaxis.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    String mUid= ds.getKey();
                    if(ds.hasChild("join")) {
                        if(ds.child("join").getValue(String.class).equals("on")){
                            Double mLatitude= ds.child("latitude").getValue(Double.class);
                            Double mLongitude= ds.child("longitude").getValue(Double.class);
                            LatLng mLatLng = new LatLng(mLatitude, mLongitude);
                            String taxi_type = ds.child("moto_type").getValue(String.class) == null ? "0" : ds.child("moto_type").getValue(String.class);
                            Taxi _mTaxi = new Taxi(mLatLng,mUid, taxi_type);
                            mTaxis.add(_mTaxi);
                            Log.d("datasaLa:", mUid);
                        }
                    }
                    Log.d("datasaLatitude:", mUid);
                }
                mapFragment.getMapAsync(FindDriverActivity.this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void requestRide() {
        if (!userPhone.equals("no")/* && !driverPhone.equals("no") && !drivertoken.equals("no")*/) {
            mDatabase.getReference("ride/" + uniqueId + "/id").setValue(uniqueId);
//            mDatabase.getReference("ride/" + uniqueId + "/driver").setValue(driverUid);
            mDatabase.getReference("ride/" + uniqueId + "/trip_type").setValue(Common.getInstance().getOption());
            mDatabase.getReference("ride/" + uniqueId + "/passenger").setValue(mAuth.getUid());
            mDatabase.getReference("ride/" + uniqueId + "/locations").setValue(locations);
            mDatabase.getReference("ride/" + uniqueId + "/price").setValue(payPrice);
            mDatabase.getReference("ride/" + uniqueId + "/comment").setValue(Common.getInstance().getComment());
            mDatabase.getReference("ride/" + uniqueId + "/distance").setValue(Common.getInstance().getDistance());
            mDatabase.getReference("ride/" + uniqueId + "/date").setValue(currentDateandTime);
            mDatabase.getReference("ride/" + uniqueId + "/paytype").setValue(pay_type);
            mDatabase.getReference("ride/" + uniqueId + "/passengernumber").setValue(userPhone);
            mDatabase.getReference("ride/" + uniqueId + "/phone_token").setValue(Common.getInstance().getUserPhone_token());
            mDatabase.getReference("ride/" + uniqueId + "/avatar").setValue(Common.getInstance().getAvatar());
            mDatabase.getReference("ride/" + uniqueId + "/passenger_name").setValue(Common.getInstance().getUserName());
            mDatabase.getReference("ride/" + uniqueId + "/passenger_offer").setValue("yes");
            mDatabase.getReference("ride/" + uniqueId + "/offer_type").setValue(Common.getInstance().getOfferType());
            mDatabase.getReference("ride/" + uniqueId + "/review").setValue(Common.getInstance().getReview());
            mDatabase.getReference("ride/" + uniqueId + "/status").setValue("waiting");
            Common.getInstance().setRide_uuid(uniqueId);
            mCancel.setEnabled(true);
            Log.d("status:", "Get all data");
        }
    }

    @Override
    public void onBackPressed() {
        mDatabase.getReference("ride/" + uniqueId).removeValue();
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(mMap == null) {
            mMap = googleMap;
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 14));
        }
        googleMap.clear();

        for(Taxi oneTaxi : mTaxis){
            mMyLocation.setLatitude(myLocation.latitude);
            mMyLocation.setLongitude(myLocation.longitude);
            mTaxiLocation.setLatitude(oneTaxi.getmTaxiLocation().latitude);
            mTaxiLocation.setLongitude(oneTaxi.getmTaxiLocation().longitude);
            if(mMyLocation.distanceTo(mTaxiLocation) < 5000){
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(oneTaxi.getmTaxiLocation());
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                googleMap.addMarker(markerOptions);
            }
        }

        MarkerOptions start_markerOptions = new MarkerOptions();
        MarkerOptions end_markerOptions = new MarkerOptions();
        start_markerOptions.position(locations.get(0));
        start_markerOptions.title(start_addresses);
        start_markerOptions.icon(BitmapDescriptorFactory.fromBitmap(start_smallMarker));
        googleMap.addMarker(start_markerOptions);

        end_markerOptions.icon(BitmapDescriptorFactory.fromBitmap(end_smallMarker));
        for(int i = 1; i <locations.size(); i++) {
            end_markerOptions.position(locations.get(i));
            end_markerOptions.title(end_addresses);
            googleMap.addMarker(end_markerOptions);
        }

//        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(start_location, 14));
    }

    private void drawRoute() {
        if(mMap != null) {
            mMap.clear();
        }
//        onSetMarker();
        showTaxi();
        LatLng origin = locations.get(0);
        LatLng dest = locations.get(locations.size() - 1);
        String url = getDirectionsUrl(origin, dest);
        url = url + "&key=AIzaSyBBymj4yxxTiMRQ6qAHndQBVbBpoUCdp94";
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";

        String waypoints = "";
        for(int i=1;i<locations.size() - 1;i++){
            LatLng point  = (LatLng) locations.get(i);
            if(i==1)
                waypoints = "waypoints=";
            waypoints += point.latitude + "," + point.longitude + "|";
        }

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode + "&" + waypoints;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    private class DownloadTask extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] urls) {
            String data = "";
            String url = String.valueOf(urls[0]);
            try {
                data = downloadUrl(url);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            String res = String.valueOf(result);
            parserTask.execute(res);
        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            org.json.JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new org.json.JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                Log.d("ParserTask", jObject.toString());

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap point = path.get(j);
                    if(point.get("lat") != null && point.get("lng") != null) {
                        double lat = Double.parseDouble(point.get("lat").toString());
                        double lng = Double.parseDouble(point.get("lng").toString());
                        LatLng position = new LatLng(lat, lng);
                        points.add(position);
                    }
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.BLACK);
                lineOptions.geodesic(true);

            }

            if(lineOptions != null && mMap != null) {
                mMap.addPolyline(lineOptions);
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for(int i = 0; i < locations.size(); i++) {
                    builder.include(locations.get(i));
                }
                LatLngBounds bounds = builder.build();
                int padding = 100; // offset from edges of the map in pixels
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                mMap.animateCamera(cu);
            }
        }
    }
}