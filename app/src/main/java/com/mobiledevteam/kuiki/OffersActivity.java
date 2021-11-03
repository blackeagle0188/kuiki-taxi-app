 package com.mobiledevteam.kuiki;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mobiledevteam.kuiki.Adapter.DeliveryRequest;
import com.mobiledevteam.kuiki.Adapter.DeliveryRequestAdapter;
import com.mobiledevteam.kuiki.Adapter.Offers;
import com.mobiledevteam.kuiki.Adapter.OffersAdapter;
import com.mobiledevteam.kuiki.data.RestaurantData;
import com.mobiledevteam.kuiki.data.ServicePaymentData;
import com.mobiledevteam.kuiki.data.SupermarketData;
import com.nanchen.ncswitchmultibutton.NCSwitchMultiButton;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Locale.getDefault;

public class OffersActivity extends AppCompatActivity implements LocationListener {

    ImageView ivHeaderPhoto;
    TextView ivHeadName;
    TextView ivHeadBrand;
    private DrawerLayout mDrawer;
    private NavigationView nvDrawer;
    LinearLayout btn_profile;
    LinearLayout btn_offers;
    LinearLayout btn_setting;
    LinearLayout btn_safety;
    LinearLayout btn_help;
    LinearLayout btn_support;
    LinearLayout layout_find_offers;
    LinearLayout layout_find_delivery;
    Button btn_mode;
    GridView offer_grid;
    GridView delivery_grid;
    private TextView txt_search_trip;
    private TextView txt_search_delivery;
    private NCSwitchMultiButton switchButton;
    Button btn_menu;
    private RelativeLayout driver_offers;
    private RelativeLayout delivery_offers;
    private TabLayout layout_tab;

    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    DatabaseReference mRef;
    private LocationManager locationManager;
    private LatLng my_location;
    private String my_city;

    private Geocoder geocoder;

    private List<Address> start_addresses;
    private List<Address> end_addresses;
    private ArrayList<LatLng> locations;

    ArrayList<Offers> mTripLists = new ArrayList<>();
    ArrayList<DeliveryRequest> mDeliveryLists = new ArrayList<>();
    OffersAdapter adapter_event;
    DeliveryRequestAdapter delivery_event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);

        geocoder = new Geocoder(this, getDefault());

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
//        FirebaseInstallations.getInstance().getToken(false).addOnCompleteListener(
//                task -> {
//                    if(task.isSuccessful()) {
//                        String phone_token = task.getResult().getToken();
//                        Common.getInstance().setPhone_token(phone_token);
//                    }
//                }
//        );

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        String token = task.getResult();
                        Common.getInstance().setPhone_token(token);
                    }
                });

        locations = new ArrayList<>();

        adapter_event = new OffersAdapter(getBaseContext(), mTripLists);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_driver_layout);
        nvDrawer = (NavigationView) findViewById(R.id.driverView);
        NavigationView navigationView = (NavigationView) findViewById(R.id.driverView);
        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_driver);
        ivHeaderPhoto = headerLayout.findViewById(R.id.avatar);
        ivHeadName = headerLayout.findViewById(R.id.user_name);
        ivHeadBrand = headerLayout.findViewById(R.id.driver_car_brand);
        btn_profile = (LinearLayout) headerLayout.findViewById(R.id.btn_go_profile);
        btn_offers = (LinearLayout) headerLayout.findViewById(R.id.btn_user_trip);
        btn_safety = (LinearLayout) headerLayout.findViewById(R.id.btn_driver_privacy);
        btn_setting = (LinearLayout) headerLayout.findViewById(R.id.btn_driver_setting);
        btn_help = (LinearLayout) headerLayout.findViewById(R.id.btn_driver_help);
        btn_support = (LinearLayout) headerLayout.findViewById(R.id.btn_driver_support);
        btn_mode = (Button)findViewById(R.id.btn_passenger_mode);
        btn_menu = (Button) findViewById(R.id.btn_menu);
        layout_find_offers = (LinearLayout) findViewById(R.id.trip_loader);
        layout_find_delivery = (LinearLayout) findViewById(R.id.delivery_loader);
        txt_search_trip = (TextView) findViewById(R.id.txt_search_trip);
        txt_search_delivery = (TextView) findViewById(R.id.txt_search_delivery);

        layout_tab = (TabLayout) findViewById(R.id.layout_tab);
        driver_offers = (RelativeLayout) findViewById(R.id.driver_offer_layout);
        delivery_offers = (RelativeLayout) findViewById(R.id.delivery_offer_layout);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        String moto_type = Common.getInstance().getMotoType();
        if(moto_type.equals("1")) {
            layout_tab.setVisibility(View.GONE);
            driver_offers.setVisibility(View.GONE);
            delivery_offers.setVisibility(View.VISIBLE);
            layout_find_delivery.setVisibility(View.VISIBLE);
        }


        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        my_location = new LatLng(48.85299, 2.34288);
        Location LocationGps;
        String provider = locationManager.getBestProvider(new Criteria(), true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        } else {
            LocationGps = locationManager.getLastKnownLocation(provider);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 30, this);
        }
        if (LocationGps != null) {
            my_location = new LatLng(LocationGps.getLatitude(), LocationGps.getLongitude());
            mDatabase.getReference("user/" + mAuth.getUid() + "/latitude").setValue(my_location.latitude);
            mDatabase.getReference("user/" + mAuth.getUid() + "/longitude").setValue(my_location.longitude);

        } else {
            my_location = new LatLng(48.85299, 2.34288);
        }


        try {
            List<Address> my_address = geocoder.getFromLocation(my_location.latitude, my_location.longitude, 1);
            my_city = my_address.get(0).getLocality();
        } catch (IOException e) {
            e.printStackTrace();
        }

        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawer.openDrawer(nvDrawer);
            }
        });

        layout_tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                switch (pos){
                    case 0:
                        driver_offers.setVisibility(View.VISIBLE);
                        delivery_offers.setVisibility(View.GONE);
                        break;
                    case 1:
                        driver_offers.setVisibility(View.GONE);
                        delivery_offers.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        onGetUserInfo();
        onSetMenuEvent();
        setSwitch();

        offer_grid = (GridView) findViewById(R.id.offer_grid);
        delivery_grid = (GridView) findViewById(R.id.delivery_grid);

        offer_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), MainDriverActivity.class);
                intent.putExtra("id", mTripLists.get(i).getId());
                intent.putExtra("type", "new");
                startActivity(intent);
                finish();
            }
        });
        delivery_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), MainDeliveryActivity.class);
                intent.putExtra("type", "new");
                intent.putExtra("id", mDeliveryLists.get(i).getId());
                startActivity(intent);
//                finish();
            }
        });

        getTripData();
        getDeliveryData();
        Common.getInstance().setDriver_uid(mAuth.getUid());
    }

    private void setSwitch() {
        switchButton = (NCSwitchMultiButton) findViewById(R.id.switch_button);
        switchButton.setVisibility(View.VISIBLE);
        mRef= mDatabase.getReference("user/"+mAuth.getUid()+"/join");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String joinStatus = dataSnapshot.getValue(String.class);
                Log.d("joinStatus11111:::", joinStatus);
                Common.getInstance().setJon_status(joinStatus);
                if(joinStatus.equals("on")){
                    switchButton.setSelectedTab(1).setBackgroundColor(getResources().getColor(R.color.colorGreen));
                    switchButton.setBackground(getResources().getDrawable(R.drawable.green_border));
                    Common.getInstance().setJoin(true);
                    txt_search_trip.setText(getResources().getString(R.string.string_find_offer));
                    txt_search_delivery.setText(getResources().getString(R.string.string_find_offer));
                }else {
                    switchButton.setSelectedTab(0).setBackgroundColor(getResources().getColor(R.color.colorRed));
                    switchButton.setBackground(getResources().getDrawable(R.drawable.red_border));
                    Common.getInstance().setJoin(false);
                    txt_search_trip.setText(getResources().getString(R.string.string_busy_status));
                    txt_search_delivery.setText(getResources().getString(R.string.string_busy_status));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        switchButton.setOnSwitchListener(new NCSwitchMultiButton.OnSwitchListener() {
            @Override
            public void onSwitch(int i, @NotNull String s) {
                if(i == 0) {
                    mDatabase.getReference("user/"+mAuth.getUid()+"/join").setValue("off");
                    offer_grid.setVisibility(View.GONE);
                    delivery_grid.setVisibility(View.GONE);
                    txt_search_trip.setText(getResources().getString(R.string.string_busy_status));
                    txt_search_delivery.setText(getResources().getString(R.string.string_busy_status));
                    layout_find_offers.setVisibility(View.VISIBLE);
                    layout_find_delivery.setVisibility(View.VISIBLE);
                } else {
                    mDatabase.getReference("user/"+mAuth.getUid()+"/join").setValue("on");
                    offer_grid.setVisibility(View.VISIBLE);
                    delivery_grid.setVisibility(View.VISIBLE);
                    txt_search_trip.setText(getResources().getString(R.string.string_find_offer));
                    txt_search_delivery.setText(getResources().getString(R.string.string_find_offer));
                    if(mTripLists.size() == 0 ) {
                        layout_find_offers.setVisibility(View.VISIBLE);
                    } else {
                        layout_find_offers.setVisibility(View.GONE);
                    }
                    if(mDeliveryLists.size() == 0 ) {
                        layout_find_delivery.setVisibility(View.VISIBLE);
                    } else {
                        layout_find_delivery.setVisibility(View.GONE);
                    }
                }
            }
        });
//        switchButton.setOnSwitchListener(new SwitchButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
//                if(isChecked) {
//                    mDatabase.getReference("user/"+mAuth.getUid()+"/join").setValue("on");
//                } else {
//                    mDatabase.getReference("user/"+mAuth.getUid()+"/join").setValue("off");
//                }
//            }
//        });
    }

    @Override
    public void onBackPressed() {

    }

    private void getTripData() {
        mRef = mDatabase.getReference("ride/");
        mRef.addChildEventListener(new ChildEventListener() {
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
                for(int i = 0; i < mTripLists.size(); i++) {
                    Offers offer = mTripLists.get(i);
                    if(offer.getId().equals(ride_uuid)) {
                        mTripLists.remove(i);
                    }
                }
                initTripView();
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
        String status = ds.child("status").getValue(String.class);
//        mTripLists.clear();
        if(status != null && status.equals("waiting")) {
            String id;
            String uid;
            String mAvatar;
            String mName;
            String mStartLocation;
            String mEndLocation;
            String mPrice;
            String mDistance;
            String mComment;
            String mReview;
            Offers offer = new Offers();

            id = ds.child("id").getValue(String.class);

            for(Offers item : mTripLists) {
                if(item.getId() == id) {
                    return;
                }
            }

            offer.setId(id);

            uid = ds.child("passenger").getValue(String.class);
            offer.setUid(uid);

            locations = new ArrayList<>();

            //Get Start Location
            Object object = ds.child("locations").getValue();
            ArrayList<LatLng> locationObj = (ArrayList<LatLng>) object;

            for (Object item : locationObj) {
                Map<String, Object> location = (Map<String, Object>) item;
                Double startLat = Double.parseDouble(location.get("latitude").toString());
                Double startLng = Double.parseDouble(location.get("longitude").toString());
                LatLng latLng = new LatLng(startLat, startLng);
                locations.add(latLng);
            }
            Common.getInstance().setLocations(locations);
            mStartLocation = "";
            try {
                LatLng start_location = locations.get(0);
                start_addresses = geocoder.getFromLocation(start_location.latitude, start_location.longitude, 1);
                String start_city = start_addresses.get(0).getLocality();
                if(!start_city.equals(my_city)) {
                    return;
                }
                String start_postalCode = start_addresses.get(0).getPostalCode();
                String start_knownName = start_addresses.get(0).getFeatureName();
                String start_throughFare = start_addresses.get(0).getThoroughfare();
                mStartLocation = start_throughFare + ", " + start_city;
                offer.setStartLocation(mStartLocation);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String destName = "";
            for(int i = 1; i < locations.size(); i++) {
                LatLng end_location = locations.get(i);
                try {
                    end_addresses = geocoder.getFromLocation(end_location.latitude, end_location.longitude, 1);
                    if(destName.equals("")) {
                        destName = end_addresses.get(0).getThoroughfare() + ", " + end_addresses.get(0).getLocality();
                    } else {
                        destName += " - " + end_addresses.get(0).getThoroughfare() + ", " + end_addresses.get(0).getLocality();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            offer.setEndLocation(destName);

            mAvatar = ds.child("avatar").getValue(String.class);
            offer.setImage(mAvatar);

            mName = ds.child("passenger_name").getValue(String.class);
            if(mName.split("\\w+").length>1){

                String lastName = mName.substring(mName.lastIndexOf(" ")+1);
                String firstName = mName.substring(0, mName.lastIndexOf(' '));
                lastName = lastName.substring(0, 1) + ".";
                mName = firstName + " " + lastName;
            }
            offer.setName(mName);

            mPrice = ds.child("price").getValue().toString() + "MXN";
            offer.setPrice(mPrice);

            try {
                mDistance = ds.child("distance").getValue().toString();
                offer.setDistance(mDistance);
            } catch (Exception e) {
                offer.setDistance("0");
            }


            mComment = ds.child("comment").getValue().toString();
            offer.setComment(mComment);
            mReview = ds.child("review").getValue().toString();
            offer.setReview(mReview);

            mTripLists.add(offer);
            initTripView();
        } else {
            for(int i = 0; i < mTripLists.size(); i++) {
                Offers offer = mTripLists.get(i);
                if(offer.getId().equals(ds.getKey())) {
                    mTripLists.remove(i);
                }
            }
            initTripView();
        }
    }

    private void getDeliveryData() {
        mRef = mDatabase.getReference("delivery/");
        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot ds, @Nullable String s) {
                getDeliveryLists(ds);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot ds, @Nullable String previousChildName) {
                getDeliveryLists(ds);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String ride_uuid = snapshot.getKey();
                for(int i = 0; i < mDeliveryLists.size(); i++) {
                    DeliveryRequest offer = mDeliveryLists.get(i);
                    if(offer.getId().equals(ride_uuid)) {
                        mDeliveryLists.remove(i);
                        Common.getInstance().setDeliveryRequests(mDeliveryLists);
                    }
                }
                initDeliveryView();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void getDeliveryLists(DataSnapshot ds) {
        String status = ds.child("status").getValue(String.class);
        if(status != null && status.equals("waiting")) {
            int delivery_type;
            delivery_type = ds.child("delivery_type").getValue(Integer.class);

            switch (delivery_type) {
                case 0:
                    getServicePaymentList(ds, delivery_type);
                    break;
                case 1:
                    getRestaurantList(ds, delivery_type);
                    break;
                case 2:
                    getSupermarketList(ds, delivery_type);
                    break;
            }
        } else {
            for(int i = 0; i < mTripLists.size(); i++) {
                Offers offer = mTripLists.get(i);
                if(offer.getId().equals(ds.getKey())) {
                    mTripLists.remove(i);
                }
            }
            initDeliveryView();
        }
    }

    private void getServicePaymentList(DataSnapshot ds, int type) {
        String key;
        String passenger_id;
        String service_company;
        String service_number;
        String passenger_name;
        String avatar;
        String pickup_pos;
        String delivery_pos;
        String review;
        int price;

        key = ds.getKey();
        passenger_id = ds.child("user").getValue(String.class);
        passenger_name = ds.child("user_name").getValue(String.class);
        service_company = ds.child("service_company").getValue(String.class);
        service_number = ds.child("service_number").getValue(String.class);
        avatar = ds.child("avatar").getValue(String.class);
        price = ds.child("price").getValue(Integer.class);
        pickup_pos = ds.child("pickup_pos").getValue(String.class);
        delivery_pos = ds.child("delivery_pos").getValue(String.class);
        review = ds.child("review").getValue(String.class);

        DeliveryRequest deliveryRequest = new DeliveryRequest();
        ServicePaymentData servicePayment = new ServicePaymentData();

        deliveryRequest.setId(key);
        deliveryRequest.setType(0);
        deliveryRequest.setUserId(passenger_id);
        deliveryRequest.setUserName(passenger_name);
        deliveryRequest.setUserAvatar(avatar);
        deliveryRequest.setUserAddress(pickup_pos);
        deliveryRequest.setReview(review);


        servicePayment.setServiceCompany(service_company);
        servicePayment.setServiceNumber(service_number);
        servicePayment.setPrice(price);

        for(DeliveryRequest item : mDeliveryLists) {
            if(item.getId() == key) {
                return;
            }
        }

        servicePayment.setPickUp(pickup_pos);

        servicePayment.setDelivery(delivery_pos);

        deliveryRequest.setServicePayment(servicePayment);

        mDeliveryLists.add(deliveryRequest);
        Common.getInstance().setDeliveryRequests(mDeliveryLists);
        initDeliveryView();
    }

    private void getRestaurantList(DataSnapshot ds, int type) {
        String key;
        String passenger_id;
        String restaurant_name;
        String orders;
        String passenger_name;
        String avatar;
        String pickup_pos;
        String delivery_pos;
        String review;

        key = ds.getKey();
        passenger_id = ds.child("user").getValue(String.class);
        passenger_name = ds.child("user_name").getValue(String.class);
        restaurant_name = ds.child("restaurant").getValue(String.class);
        orders = ds.child("orders").getValue(String.class);
        avatar = ds.child("avatar").getValue(String.class);
        pickup_pos = ds.child("pickup_pos").getValue(String.class);
        review = ds.child("review").getValue(String.class);

        DeliveryRequest deliveryRequest = new DeliveryRequest();
        RestaurantData restaurant = new RestaurantData();

        deliveryRequest.setId(key);
        deliveryRequest.setType(1);
        deliveryRequest.setUserId(passenger_id);
        deliveryRequest.setUserName(passenger_name);
        deliveryRequest.setUserAvatar(avatar);
        deliveryRequest.setUserAddress(pickup_pos);
        deliveryRequest.setReview(review);

        restaurant.setRestaurantname(restaurant_name);
        restaurant.setOrder(orders);

        for(DeliveryRequest item : mDeliveryLists) {
            if(item.getId() == key) {
                return;
            }
        }

        restaurant.setPickUp(pickup_pos);

        deliveryRequest.setRestaurant(restaurant);

        mDeliveryLists.add(deliveryRequest);
        Common.getInstance().setDeliveryRequests(mDeliveryLists);
        initDeliveryView();
    }

    private void getSupermarketList(DataSnapshot ds, int type) {
        String key;
        String passenger_id;
        String restaurant_name;
        String orders;
        String passenger_name;
        String avatar;
        String pickup_pos;
        String delivery_pos;
        String review;

        key = ds.getKey();
        passenger_id = ds.child("user").getValue(String.class);
        passenger_name = ds.child("user_name").getValue(String.class);
        restaurant_name = ds.child("supermarket").getValue(String.class);
        orders = ds.child("orders").getValue(String.class);
        avatar = ds.child("avatar").getValue(String.class);
        pickup_pos = ds.child("pickup_pos").getValue(String.class);
        delivery_pos = ds.child("delivery_pos").getValue(String.class);
        review = ds.child("review").getValue(String.class);

        DeliveryRequest deliveryRequest = new DeliveryRequest();
        SupermarketData supermarket = new SupermarketData();

        deliveryRequest.setId(key);
        deliveryRequest.setType(2);
        deliveryRequest.setUserId(passenger_id);
        deliveryRequest.setUserName(passenger_name);
        deliveryRequest.setUserAvatar(avatar);
        deliveryRequest.setUserAddress(pickup_pos);
        deliveryRequest.setReview(review);

        supermarket.setSupermarketname(restaurant_name);
        supermarket.setOrder(orders);

        for(DeliveryRequest item : mDeliveryLists) {
            if(item.getId() == key) {
                return;
            }
        }

        supermarket.setPickUp(pickup_pos);

        supermarket.setDelivery(delivery_pos);

        deliveryRequest.setSupermarket(supermarket);

        mDeliveryLists.add(deliveryRequest);
        Common.getInstance().setDeliveryRequests(mDeliveryLists);
        initDeliveryView();
    }

    private void initTripView(){
        if(mTripLists.size() == 0 ) {
            layout_find_offers.setVisibility(View.VISIBLE);
        } else {
            layout_find_offers.setVisibility(View.GONE);
        }
        adapter_event = new OffersAdapter(getBaseContext(), mTripLists);
        offer_grid.setAdapter(adapter_event);
    }

    private void initDeliveryView(){
        if(mDeliveryLists.size() == 0 ) {
            layout_find_delivery.setVisibility(View.VISIBLE);
        } else {
            layout_find_delivery.setVisibility(View.GONE);
        }
        delivery_event = new DeliveryRequestAdapter(getBaseContext(), mDeliveryLists);
        delivery_grid.setAdapter(delivery_event);
    }

    private void onGetUserInfo() {
        mRef = mDatabase.getReference("user/" + mAuth.getUid() + "/avatar");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String avatar_link = dataSnapshot.getValue(String.class);
                if (avatar_link != null && avatar_link != "") {
                    Common.getInstance().setAvatar(avatar_link);
                    onLocaAvatar(avatar_link);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mRef = mDatabase.getReference("user/" + mAuth.getUid() + "/review");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String review = dataSnapshot.getValue(String.class);
                if (review != null && review != "") {
                    Common.getInstance().setReview(review);
                } else {
                    Common.getInstance().setReview("5.0 (0)");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mRef = mDatabase.getReference("user/" + mAuth.getUid() + "/username");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String username = dataSnapshot.getValue(String.class);
                if (username != null && username != "") {
                    Common.getInstance().setUserName(username);
                    ivHeadName.setText(username);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mRef = mDatabase.getReference("user/" + mAuth.getUid() + "/brand");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String brand = dataSnapshot.getValue(String.class);
                if (brand != null && brand != "") {
                    Common.getInstance().setBrand(brand);
                    ivHeadBrand.setText(brand);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void onLocaAvatar(String url) {
        Picasso.get().load(Common.getInstance().getBaseURL() + "backend/" + url).into(ivHeaderPhoto);
    }

    private void onSetMenuEvent() {
        btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), DriverProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btn_offers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent=new Intent(getApplicationContext(), OffersActivity.class);
//                startActivity(intent);
//                finish();
            }
        });
        btn_safety.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), DriverProtectionActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), DriverSettingActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btn_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent=new Intent(getApplicationContext(), DriverSettingActivity.class);
//                startActivity(intent);
//                finish();
            }
        });
        btn_support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        btn_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMode();
            }
        });
    }

    private void openMode(){
        if(Common.getInstance().getJon_status().equals("on")){
            Toast.makeText(OffersActivity.this, "Please Offline.", Toast.LENGTH_LONG ).show();
        }else{
            mDatabase.getReference("user/"+mAuth.getUid()+"/type").setValue("user");
            mDatabase.getReference("user/"+mAuth.getUid()+"/join").setValue("off");
            Intent intent=new Intent(getBaseContext(), HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        mDatabase.getReference("user/"+mAuth.getUid()+"/latitude").setValue(location.getLatitude());
        mDatabase.getReference("user/"+mAuth.getUid()+"/longitude").setValue(location.getLongitude());
        my_location = new LatLng(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent intent;
            switch (item.getItemId()) {
                case R.id.navigation_offers:
                    intent  = new Intent(getApplicationContext(), OffersActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                case R.id.navigation_income:
                    intent  = new Intent(getApplicationContext(), TripHistoryActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                case R.id.navigation_payment:
//                    Intent intent  = new Intent(getApplicationContext(), OffersActivity.class);
//                    startActivity(intent);
//                    finish();
                    return true;
                case R.id.navigation_reviews:
//                    Intent intent  = new Intent(getApplicationContext(), OffersActivity.class);
//                    startActivity(intent);
//                    finish();
                    return true;
            }
            return false;
        }
    };
}