package com.mobiledevteam.kuiki;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobiledevteam.kuiki.Adapter.DeliveryRequest;
import com.mobiledevteam.kuiki.Adapter.Offers;
import com.mobiledevteam.kuiki.data.RestaurantData;
import com.mobiledevteam.kuiki.data.ServicePaymentData;
import com.mobiledevteam.kuiki.data.SupermarketData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Locale.getDefault;

public class CheckUser extends AppCompatActivity implements LocationListener {
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private LocationManager locationmanager;
    String user_status = "";
    String user_type ="";
    String moto_type = "0";
    private ArrayList<LatLng> locations;
    private Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_user);
        if(!Places.isInitialized()){
            Places.initialize(getBaseContext(),"AIzaSyBBymj4yxxTiMRQ6qAHndQBVbBpoUCdp94");
        }
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        locationmanager = (LocationManager) getSystemService(LOCATION_SERVICE);
        geocoder = new Geocoder(this, getDefault());
        String provider = locationmanager.getBestProvider(new Criteria(), true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10, this);
        checkUser();
    }
    @Override
    public void onBackPressed() {
    }
    private void checkUser(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            Log.d("user uid:", currentUser.getUid());
            mRef = mDatabase.getReference("user/"+mAuth.getUid());
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    user_status = snapshot.child("status").getValue(String.class);
                    user_type = snapshot.child("type").getValue(String.class);
                    if(snapshot.hasChild("moto_type")) {
                        moto_type = snapshot.child("moto_type").getValue(String.class);
                    } else {
                        moto_type = "0";
                    }
                    Common.getInstance().setMotoType(moto_type);

                    if(user_status != null) {
                        if(user_status.equals("on")){
                            if(user_type.equals("driver")){
                                onCheckOldOffer();
                            }else{
                                onCheckOldTrip();
                            }
                        }else{
                            Intent intent = new Intent(CheckUser.this, PhoneVerify.class);
                            startActivity(intent);
                            finish();
                        }
                    } else{
                        Intent intent = new Intent(CheckUser.this, PhoneVerify.class);
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else{
            Intent intent = new Intent(CheckUser.this, PhoneVerify.class);
            startActivity(intent);
            finish();
        }
    }

    private void onCheckOldTrip() {
        mRef = FirebaseDatabase.getInstance().getReference().child("ride/");
        mRef.orderByChild("passenger").equalTo(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for(DataSnapshot ds : snapshot.getChildren()) {
                        String key = ds.getKey();
                        onGetOldTrip(key, null, true);
                    }
                } else {
                    onCheckOldDelivery();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void onGetOldTrip(String key, String offer, boolean type) {
        if(type) {
            mRef = mDatabase.getReference("ride/" + key);
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String key = snapshot.getKey();
                        getTripLists(key, snapshot, type);
                    } else {

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            mRef = mDatabase.getReference("ride/" + key);
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String key = snapshot.getKey();
                        getTripLists(key, snapshot, type);
                    } else {
                        mDatabase.getReference("offer/"+offer).removeValue();
                        Intent intent = new Intent(CheckUser.this, OffersActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void onCheckOldDelivery() {
        mRef = FirebaseDatabase.getInstance().getReference().child("delivery/");
        mRef.orderByChild("user").equalTo(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for(DataSnapshot ds : snapshot.getChildren()) {
                        String key = ds.getKey();
                        onGetOldDelivery(key, null, true);
                    }
                } else {
                    Intent intent = new Intent(CheckUser.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void onGetOldDelivery(String key, String offer, boolean type) {
        if(type) {
            mRef = mDatabase.getReference("delivery/" + key);
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
//                        String key = snapshot.getKey();
                        getDeliveryList(key, snapshot, type, offer);
                    } else {

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            mRef = mDatabase.getReference("delivery/" + key);
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String key = snapshot.getKey();
                        getDeliveryList(key, snapshot, type, offer);
                    } else {
                        mDatabase.getReference("deliveryoffer/"+offer).removeValue();
                        Intent intent = new Intent(CheckUser.this, OffersActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void getDeliveryList(String key, DataSnapshot ds, boolean type, String offer_id) {
        String uid;
        String mName;
        String mAvatar;
        String mPhone;
        String mPos;
        String mReview;
        String mStatus;
        String mDriver;

        int mType = 0;

        mType = ds.child("delivery_type").getValue(Integer.class);
        mStatus = ds.child("status").getValue(String.class);
        if(mStatus.equals("complete")) {
            if(type) {
                Intent intent = new Intent(CheckUser.this, HomeActivity.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(CheckUser.this, OffersActivity.class);
                startActivity(intent);
                finish();
            }

            return;
        }

        DeliveryRequest deliveryRequest = new DeliveryRequest();

        deliveryRequest.setId(key);
        deliveryRequest.setType(mType);

        uid = ds.child("user").getValue(String.class);
        Common.getInstance().setUser_uid(uid);
        mName = ds.child("user_name").getValue(String.class);
        mAvatar = ds.child("avatar").getValue(String.class);
        mPos = ds.child("pickup_pos").getValue(String.class);
        mReview = ds.child("review").getValue(String.class);
        mDriver = ds.child("driver").getValue(String.class);
        Common.getInstance().setDriver_uid(mDriver);


        deliveryRequest.setUserId(uid);
        deliveryRequest.setUserName(mName);
        deliveryRequest.setUserAvatar(mAvatar);
        deliveryRequest.setUserAddress(mPos);
        deliveryRequest.setReview(mReview);

        if(mType == 0) {
            ServicePaymentData servicePayment = new ServicePaymentData();
            String service_company = ds.child("service_company").getValue(String.class);
            String service_number = ds.child("service_number").getValue(String.class);
            int price = ds.child("price").getValue(Integer.class);
            servicePayment.setServiceCompany(service_company);
            servicePayment.setServiceNumber(service_number);
            servicePayment.setPrice(price);
            servicePayment.setPickUp(mPos);

            deliveryRequest.setServicePayment(servicePayment);

            Common.getInstance().setServicePayment(servicePayment);
            Common.getInstance().setServiceType(0);
        } else if(mType == 1) {
            RestaurantData restaurant = new RestaurantData();
            String restaurantName = ds.child("restaurant").getValue(String.class);
            String restaurantOrder = ds.child("orders").getValue(String.class);
            String restaurantAddress = ds.child("pickup_pos").getValue(String.class);
            restaurant.setRestaurantname(restaurantName);
            restaurant.setOrder(restaurantOrder);
            restaurant.setPickUp(restaurantAddress);


            deliveryRequest.setRestaurant(restaurant);
            Common.getInstance().setRestaurant(restaurant);
            Common.getInstance().setServiceType(1);
        } else if(mType == 2) {
            SupermarketData supermarket = new SupermarketData();
            String supermarketName = ds.child("supermarket").getValue(String.class);
            String supermarketOrder = ds.child("orders").getValue(String.class);
            String supermarketAddress = ds.child("pickup_pos").getValue(String.class);
            supermarket.setSupermarketname(supermarketName);
            supermarket.setOrder(supermarketOrder);
            supermarket.setPickUp(supermarketAddress);
            deliveryRequest.setSupermarket(supermarket);
            Common.getInstance().setSupermarket(supermarket);
            Common.getInstance().setServiceType(2);
        }
        ArrayList<DeliveryRequest> mDeliveryLists = new ArrayList<>();
        mDeliveryLists.add(deliveryRequest);
        Common.getInstance().setDeliveryRequests(mDeliveryLists);
        if(type) {
            if(mStatus.equals("waiting")) {
                Intent intent = new Intent(getApplicationContext(), FindDeliveryActivity.class);
                intent.putExtra("create", key);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(getApplicationContext(), UserDeliveryActivity.class);
                intent.putExtra("create", key);
                startActivity(intent);
                finish();
            }
        } else {
            Intent intent = new Intent(CheckUser.this, MainDeliveryActivity.class);
            intent.putExtra("id", key);
            intent.putExtra("offer_id", offer_id);
            intent.putExtra("type", "old");
            startActivity(intent);
            finish();
        }
    }

    private void getTripLists(String key, DataSnapshot ds, boolean type) {
            String id;
            String uid;
            String mAvatar;
            String mName;
            String mStartLocation;
            int mPrice;
            int offer_type;
            String mPayType;
            String mComment;

            String status = ds.child("status").getValue(String.class);

            if(type) {
                uid = ds.child("passenger").getValue(String.class);
            } else {
                uid = ds.child("driver").getValue(String.class);
            }

            if(!uid.equals(mAuth.getUid())) {
                return;
            }

            id = ds.child("id").getValue(String.class);

            uid = ds.child("passenger").getValue(String.class);
            Common.getInstance().setUser_uid(uid);
            uid = ds.child("driver").getValue(String.class);
            Common.getInstance().setDriver_uid(uid);

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
                List<Address> start_addresses = geocoder.getFromLocation(start_location.latitude, start_location.longitude, 1);
                String start_city = start_addresses.get(0).getLocality();
                String start_postalCode = start_addresses.get(0).getPostalCode();
                String start_knownName = start_addresses.get(0).getFeatureName();
                String start_throughFare = start_addresses.get(0).getThoroughfare();
                mStartLocation = start_knownName + " " + start_throughFare + ", " + start_postalCode + " " + start_city;
                Common.getInstance().setStart_address(mStartLocation);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String destName = "";
            for(int i = 1; i < locations.size(); i++) {
                LatLng end_location = locations.get(i);
                try {
                    List<Address> end_addresses = geocoder.getFromLocation(end_location.latitude, end_location.longitude, 1);
                    if(destName.equals("")) {
                        destName = end_addresses.get(0).getThoroughfare() + ", " + end_addresses.get(0).getLocality();
                    } else {
                        destName += " - " + end_addresses.get(0).getThoroughfare() + ", " + end_addresses.get(0).getLocality();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Common.getInstance().setEnd_address(destName);

            mAvatar = ds.child("avatar").getValue(String.class);

            mName = ds.child("passenger_name").getValue(String.class);

            mPayType = ds.child("paytype").getValue(String.class);
            Common.getInstance().setPay_type(mPayType);

            mPrice = ds.child("price").getValue(Integer.class);
            Common.getInstance().setPay_amount(mPrice);

            offer_type = ds.child("offer_type").getValue(Integer.class);
            Common.getInstance().setOfferType(offer_type);

//            mDistance = ds.child("distance").getValue().toString();

            mComment = ds.child("comment").getValue().toString();

            if(type) {
                if(!status.equals("waiting")) {
                    Intent intent = new Intent(CheckUser.this, UserRideActivity.class);
                    intent.putExtra("uuid", key);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(CheckUser.this, FindDriverActivity.class);
                    intent.putExtra("uuid", key);
                    startActivity(intent);
                    finish();
                }
            } else {
                Intent intent = new Intent(CheckUser.this, MainDriverActivity.class);
                intent.putExtra("id", key);
                intent.putExtra("type", "old");
                startActivity(intent);
                finish();
            }
    }

    private void onCheckOldOffer() {
        mRef = FirebaseDatabase.getInstance().getReference().child("offer/");
        mRef.orderByChild("driver").equalTo(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for(DataSnapshot ds : snapshot.getChildren()) {
                        String key = ds.getKey();
                        String ride_uid = ds.child("ride").getValue(String.class);
                        String status = ds.child("status").getValue(String.class);
                        onGetOldTrip(ride_uid, key, false);
                    }
                } else {
                    onCheckOldDelievryOffer();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void onCheckOldDelievryOffer() {
        mRef = FirebaseDatabase.getInstance().getReference().child("deliveryoffer/");
        mRef.orderByChild("driver").equalTo(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for(DataSnapshot ds : snapshot.getChildren()) {
                        String key = ds.getKey();
                        String ride_uid = ds.child("ride").getValue(String.class);
                        String status = ds.child("status").getValue(String.class);
                        onGetOldDelivery(ride_uid, key, false);
                    }
                } else {
                    Intent intent = new Intent(CheckUser.this, OffersActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("Location:", String.valueOf( location.getLatitude()));
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
}