package com.mobiledevteam.kuiki;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
//import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.installations.InstallationTokenResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.mobiledevteam.kuiki.Adapter.Taxi;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.util.Locale.getDefault;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnCameraChangeListener, GoogleMap.OnCameraMoveCanceledListener, GoogleMap.OnCameraIdleListener, GoogleMap.OnCameraMoveStartedListener {

    private GoogleMap GMap;
    private LocationManager locationManager;
    private CustomMapFragment mapFragment;
    private Geocoder geocoder;
    private List<Address> addressList;
    private LatLng my_location;
    private ArrayList<LatLng> end_location;
    private ArrayList<LatLng> locations;
    private Button _start_confirm;
    private EditText _start_location;
    private EditText _end_location;
    private EditText trip_money;
    private TextView txt_distance;
    private Button btn_payment_cash;
    private ImageView ivHeaderPhoto;
    private TextView ivHeadName;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private Bitmap smallMarker;
    private Bitmap motoMarker;
    private Location mMyLocation;
    private Location mTaxiLocation;
    private DrawerLayout mDrawer;
    private NavigationView nvDrawer;
    private LinearLayout btn_go_profile;
    private LinearLayout btn_user_home;
    private LinearLayout btn_user_ride;
    private LinearLayout btn_user_privacy;
    private LinearLayout btn_user_setting;
    private LinearLayout btn_user_help;
    private LinearLayout btn_user_support;
    private Button btn_mode;
    private View headerLayout;
    private NavigationView navigationView;
    private EditText trip_comment;
    private Button btn_menu;
    private TextView btn_bottom_ok;
    private LinearLayout btn_trip_option;
    private LinearLayout btn_delivery_option;
    private Button btn_select_location;
    private Button btn_select_destination;
    private Button btn_add_destination;
    private Button btn_clear_destination;
    private ImageView img_start_location;
    private Button txt_location_name;
    private LinearLayout layout_trip;
    private LinearLayout layout_delivery;
    private Button btn_service_payment;
    private Button btn_restaurant;
    private Button btn_supermarket;
    private Button btn_delivery;

    private Bitmap start_smallMarker;
    private Bitmap end_smallMarker;
    MarkerOptions first_markerOptions;
    MarkerOptions start_markerOptions;
    MarkerOptions end_markerOptions;
    private List<Address> start_addresses;
    private List<Address> end_addresses;
    private ArrayList<Marker> taxi_markers;


    private Double distance = 0.0;
    private int min_price;
    private int offertype;
    private int km_price;
    private boolean priceway = false;
    private int offer_price = 0;
    private boolean loaded = false;
    private String destNames = "";
    private boolean passengerOffer = true;

    ParserTask parserTask;
    private int selectLocation = 1;
    private int selectDest = 0;

    private View marker;
    private CircleImageView user_dp;
    private Marker user_marker;
    private boolean mMapIsTouched = false;
    private float zoomLevel = 0;

    private ArrayList<Taxi> mTaxis = new ArrayList<>();

    List<Place.Field> fields = Arrays.asList(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG
    );

    String phone_token = "";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        geocoder = new Geocoder(this, getDefault());
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
//        FirebaseInstallations.getInstance().getToken(false).addOnCompleteListener(
//                task -> {
//                    if(task.isSuccessful()) {
//                        String token = task.getResult().getToken();
//                        Common.getInstance().setUserPhone_token(token);
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
                        Common.getInstance().setUserPhone_token(token);
                    }
                });


        Date date = Calendar.getInstance().getTime();
        taxi_markers = new ArrayList<>();

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        navigationView = (NavigationView) findViewById(R.id.nvView);
        headerLayout = navigationView.inflateHeaderView(R.layout.nav_header);
        ivHeaderPhoto = headerLayout.findViewById(R.id.avatar);
        ivHeadName = headerLayout.findViewById(R.id.user_name);
        btn_go_profile = (LinearLayout) headerLayout.findViewById(R.id.btn_go_profile);
        btn_user_home = (LinearLayout) headerLayout.findViewById(R.id.btn_user_home);
        btn_user_ride = (LinearLayout) headerLayout.findViewById(R.id.btn_user_ride);
        btn_user_privacy = (LinearLayout) headerLayout.findViewById(R.id.btn_user_privacy);
        btn_user_setting = (LinearLayout) headerLayout.findViewById(R.id.btn_user_setting);
        btn_user_help = (LinearLayout) headerLayout.findViewById(R.id.btn_user_help);
        btn_user_support = (LinearLayout) headerLayout.findViewById(R.id.btn_user_support);
        btn_mode = (Button) findViewById(R.id.btn_passenger_mode);
        _start_confirm = (Button) findViewById(R.id.start_btn_confrim);
        _start_location = (EditText) findViewById(R.id.start_location);
        _end_location = (EditText) findViewById(R.id.end_location);
        trip_money = (EditText) findViewById(R.id.trip_money);
        trip_comment = (EditText) findViewById(R.id.trip_comment);
        btn_menu = (Button) findViewById(R.id.btn_menu);
        btn_trip_option = (LinearLayout) findViewById(R.id.btn_trip_option);
        btn_delivery_option = (LinearLayout) findViewById(R.id.btn_delivery_option);
        btn_select_location = (Button) findViewById(R.id.btn_select_location);
        btn_select_destination = (Button) findViewById(R.id.btn_select_destination);
        btn_add_destination = (Button) findViewById(R.id.btn_add_destination);
        btn_clear_destination = (Button) findViewById(R.id.btn_clear_destination);
        img_start_location = (ImageView) findViewById(R.id.img_start_location);
        txt_location_name = (Button) findViewById(R.id.txt_home_Plocation_name);
        layout_trip = (LinearLayout) findViewById(R.id.layout_trip);
        layout_delivery = (LinearLayout)findViewById(R.id.layout_delivery);

        btn_service_payment = (Button) findViewById(R.id.btn_service_payment);
        btn_restaurant = (Button) findViewById(R.id.btn_restaurant);
        btn_supermarket = (Button) findViewById(R.id.btn_supermarket);
        btn_delivery = (Button) findViewById(R.id.btn_delivery);

        locations = new ArrayList<LatLng>();

        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawer.openDrawer(nvDrawer);
            }
        });

        btn_trip_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.getInstance().setOption(0);
                btn_trip_option.setBackground(getResources().getDrawable(R.drawable.blue_border));
                btn_delivery_option.setBackground(getResources().getDrawable(R.drawable.grey_border));
                layout_trip.setVisibility(View.VISIBLE);
                layout_delivery.setVisibility(View.GONE);
            }
        });

        btn_delivery_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.getInstance().setOption(1);
                btn_trip_option.setBackground(getResources().getDrawable(R.drawable.grey_border));
                btn_delivery_option.setBackground(getResources().getDrawable(R.drawable.blue_border));
                layout_trip.setVisibility(View.GONE);
                layout_delivery.setVisibility(View.VISIBLE);
            }
        });

        btn_select_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectLocation = 1;
                Intent intent = new Intent(HomeActivity.this, SelectLocationActivity.class);
                startActivityForResult(intent, 103);
            }
        });

        btn_select_destination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDest += 1;
                selectLocation = 0;
                Intent intent = new Intent(HomeActivity.this, SelectLocationActivity.class);
                startActivityForResult(intent, 104);
            }
        });

        btn_add_destination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, SelectLocationActivity.class);
                startActivityForResult(intent, 104);
            }
        });

        btn_clear_destination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locations.clear();
                locations.add(my_location);
                GMap.clear();
                GMap.moveCamera(CameraUpdateFactory.newLatLngZoom(my_location,GMap.getCameraPosition().zoom));
                _end_location.setText("");
                destNames = "";
                showTaxi();
                btn_select_destination.setVisibility(View.VISIBLE);
                btn_clear_destination.setVisibility(View.GONE);
                btn_add_destination.setVisibility(View.GONE);
            }
        });

        mapFragment = (CustomMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.setOnDragListener(new MapWrapperLayout.OnDragListener() {
            @Override
            public void onDrag(MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mMapIsTouched = true;
                        onMapDragStarted();
                        break;
                    case MotionEvent.ACTION_UP:
                        onMapDragEnd();
                        break;
                }
            }
        });
        mapFragment.getMapAsync(HomeActivity.this);

        BitmapDrawable bitmapdraw1 = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_map_start);
        Bitmap b1 = bitmapdraw1.getBitmap();
//        start_markerOptions = new MarkerOptions();
//        start_smallMarker = Bitmap.createScaledBitmap(b1, 80, 120, false);
//        start_markerOptions.icon(BitmapDescriptorFactory.fromBitmap(start_smallMarker));
//        start_markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(HomeActivity.this)));

        BitmapDrawable bitmapdraw2 = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_map_end);
        Bitmap b2 = bitmapdraw2.getBitmap();
        end_markerOptions = new MarkerOptions();
        end_smallMarker = Bitmap.createScaledBitmap(b2, 120, 120, false);
        end_markerOptions.icon(BitmapDescriptorFactory.fromBitmap(end_smallMarker));

        BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_car_front);
        Bitmap b = bitmapDrawable.getBitmap();
        smallMarker = Bitmap.createScaledBitmap(b, 100, 100, false);
        mMyLocation = new Location("User");
        mTaxiLocation = new Location("Taxi");

        bitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_motor_option);
        b = bitmapDrawable.getBitmap();
        motoMarker = Bitmap.createScaledBitmap(b, 80, 70, false);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        my_location = new LatLng(48.85299, 2.34288);
        Location LocationGps = null;
        String provider = locationManager.getBestProvider(new Criteria(), true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        } else {
            try {
                LocationGps = locationManager.getLastKnownLocation(provider);
            } catch (Exception e) {

            }

        }
        if (LocationGps != null) {
            my_location = new LatLng(LocationGps.getLatitude(), LocationGps.getLongitude());
            mDatabase.getReference("user/" + mAuth.getUid() + "/latitude").setValue(my_location.latitude);
            mDatabase.getReference("user/" + mAuth.getUid() + "/longitude").setValue(my_location.longitude);

        } else {
            my_location = new LatLng(48.85299, 2.34288);
        }
        locations.add(my_location);
        try {
            addressList = geocoder.getFromLocation(my_location.latitude, my_location.longitude, 1);
            String city = addressList.get(0).getLocality();
            String postalCode = addressList.get(0).getPostalCode();
            String knownName = addressList.get(0).getFeatureName();
            String throughFare = addressList.get(0).getThoroughfare();
            _start_location.setText(knownName + " " + throughFare + ", " + postalCode + " " + city);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Common.getInstance().setPay_type("cash");

        getCityInfo();
        onSetMenuEvent();
        setupView();
        onGetUserInfo();

//        showTaxi();
        Common.getInstance().setUser_uid(mAuth.getUid());
    }

    private void onMapDragStarted() {
        if(selectLocation == 1) {
//            GMap.clear();
            zoomLevel = GMap.getCameraPosition().zoom;
            img_start_location.setVisibility(View.VISIBLE);
        } else {
            img_start_location.setVisibility(View.GONE);
            txt_location_name.setVisibility(View.GONE);
        }
    }

    private void onMapDragEnd() {
//        img_start_location.setVisibility(View.GONE);
//        txt_location_name.setVisibility(View.GONE );
    }

    private void getAvatarView(Context context) {
        marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);
        user_dp = (CircleImageView) marker.findViewById(R.id.user_dp);
    }

    private Bitmap createCustomMarker() {
        //reference View with image
        marker.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        marker.layout(0, marker.getMeasuredHeight(), marker.getMeasuredWidth(), marker.getMeasuredHeight());
        marker.draw(canvas);
        Common.getInstance().setBitmap(bitmap);
        return bitmap;
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == 101) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                _start_location.setText(place.getAddress());
                my_location = place.getLatLng();
                locations.set(0, my_location);
                mapFragment.getMapAsync(this);
                try {
                    start_addresses = geocoder.getFromLocation(my_location.latitude, my_location.longitude, 1);
                    String start_city = start_addresses.get(0).getLocality();
                    String start_postalCode = start_addresses.get(0).getPostalCode();
                    String start_knownName = start_addresses.get(0).getFeatureName();
                    String start_throughFare = start_addresses.get(0).getThoroughfare();
                    Common.getInstance().setStart_address(start_knownName + " " + start_throughFare + ", " + start_postalCode + " " + start_city);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(locations.size() == 1) {
                    GMap.moveCamera(CameraUpdateFactory.newLatLng(my_location));
                } else {
                    drawRoute();
                }
            } else if (requestCode == 102) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                LatLng location = place.getLatLng();
                locations.add(location);

                try {
                    end_addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1);
                    String end_city = end_addresses.get(0).getLocality();
                    String throughfare = end_addresses.get(0).getThoroughfare();
                    if(destNames.equals("")) {
                        destNames = throughfare + ", " + end_city;
                    } else {
                        destNames += " - " + throughfare + ", " + end_city;
                    }
                    Common.getInstance().setEnd_address(destNames);
                    _end_location.setText(destNames);
                    drawRoute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                locations.add(place.getLatLng());
            } else if(requestCode == 103) {
                Double lat = data.getDoubleExtra("lat", 0.0);
                Double lng = data.getDoubleExtra("lng", 0.0);
                LatLng location = new LatLng(lat, lng);
                my_location = location;
                locations.set(0, my_location);
                try {
                    start_addresses = geocoder.getFromLocation(my_location.latitude, my_location.longitude, 1);
                    String start_city = start_addresses.get(0).getLocality();
                    String throughfare = start_addresses.get(0).getThoroughfare();
                    _start_location.setText(throughfare + ", " + start_city);
                    drawRoute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if(requestCode == 104) {
                Double lat = data.getDoubleExtra("lat", 0.0);
                Double lng = data.getDoubleExtra("lng", 0.0);
                LatLng location = new LatLng(lat, lng);
                locations.add(location);
                try {
                    end_addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1);
                    String end_city = end_addresses.get(0).getLocality();
                    String throughfare = end_addresses.get(0).getThoroughfare();
                    if(destNames.equals("")) {
                        destNames = throughfare + ", " + end_city;
                    } else {
                        destNames += " - " + throughfare + ", " + end_city;
                    }
                    Common.getInstance().setEnd_address(destNames);
                    _end_location.setText(destNames);
                    drawRoute();
                    btn_select_destination.setVisibility(View.GONE);
                    btn_add_destination.setVisibility(View.VISIBLE);
                    btn_clear_destination.setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
//        googleMap.clear();
        if(GMap == null) {
            GMap = googleMap;
            showTaxi();
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(my_location, 14));
        }
        googleMap.setOnCameraChangeListener(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.setOnCameraIdleListener(this);
        googleMap.setOnCameraMoveStartedListener(this);
        googleMap.setOnCameraMoveCanceledListener(this);
//        googleMap.addMarker(new MarkerOptions().position(my_location));
//        onSetMarker();
        if(!loaded) {
            onLocaAvatar(Common.getInstance().getAvatar());
            loaded = true;
        }

        try {
            start_addresses = geocoder.getFromLocation(my_location.latitude, my_location.longitude, 1);
            String start_city = start_addresses.get(0).getLocality();
            String start_postalCode = start_addresses.get(0).getPostalCode();
            String start_knownName = start_addresses.get(0).getFeatureName();
            String start_throughFare = start_addresses.get(0).getThoroughfare();
            Common.getInstance().setStart_address(start_knownName + " " +start_throughFare+ ", " +start_postalCode + " " +start_city);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onSetMarker() {
        if(start_markerOptions == null) {
            start_markerOptions = new MarkerOptions();
            start_markerOptions.icon(BitmapDescriptorFactory.fromBitmap(Common.getInstance().getBitmap()));
        }
        if(locations.size() == 1) {
//            start_markerOptions.position(my_location);
//            GMap.addMarker(start_markerOptions);
            GMap.moveCamera(CameraUpdateFactory.newLatLngZoom(my_location,GMap.getCameraPosition().zoom));
        } else {
            start_markerOptions.position(my_location);
            GMap.addMarker(start_markerOptions);
            img_start_location.setVisibility(View.GONE);
            txt_location_name.setVisibility(View.GONE );
            for(int i = 1; i < locations.size(); i++) {
                end_markerOptions.position(locations.get(i));
                GMap.addMarker(end_markerOptions);
            }
        }
    }

    private void drawRoute() {
        if(GMap != null) {
            GMap.clear();
        }
        onSetMarker();
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

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        Log.v("TestLog", "OnCameraChange: " + cameraPosition);
    }

    @Override
    public void onCameraMoveStarted(int i) {
//        selectLocation = 1;
//        img_start_location.setVisibility(View.VISIBLE);
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

            parserTask = new ParserTask();

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
                    } else if(point.get("distance") != null) {
                        point = path.get(j);
                        distance += Double.parseDouble(point.get("distance").toString().replaceAll("[^\\d.]", ""));
                        Common.getInstance().setDistance(distance);
                    }
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.BLACK);
                lineOptions.geodesic(true);

            }

            if(lineOptions != null && GMap != null) {
                GMap.addPolyline(lineOptions);
//                start_markerOptions.position(my_location);
//                end_markerOptions.position(end_location);
//                GMap.addMarker((start_markerOptions));
//                GMap.addMarker((end_markerOptions));
//                GMap.animateCamera(CameraUpdateFactory.newLatLngZoom(my_location,14));

                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for(int i = 0; i < locations.size(); i++) {
                    builder.include(locations.get(i));
                }
                LatLngBounds bounds = builder.build();
                int padding = 100; // offset from edges of the map in pixels
                if(locations.size() == 1) {
//                    img_start_location.setVisibility(View.GONE);
                    CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(my_location, GMap.getCameraPosition().zoom);
                    GMap.animateCamera(cu);
                } else {
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    GMap.animateCamera(cu);
//                    GMap.animateCamera(CameraUpdateFactory.newLatLngZoom(my_location, GMap.getCameraPosition().zoom));
                }

                parserTask.cancel(true);
            }
        }
    }

    public void onStart() {
        super.onStart();

        Log.d("start::", String.valueOf(navigationView.getHeight()));
    }

    private void setupView(){
        _start_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectLocation = 1;
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY , fields).build(getBaseContext());
                startActivityForResult(intent,101);
            }
        });
        _end_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectLocation = 0;

                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(getBaseContext());
                startActivityForResult(intent,102);
            }
        });
        trip_money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(locations.size() > 1) {
                    showPriceInputDialog();
                }
            }
        });
        _start_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(locations.size() > 1) {
//                    startConfirm();
                    SearchDriver();
                }
            }
        });

        btn_service_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ServicePaymentActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btn_restaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RestaurantActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btn_supermarket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SupermarketActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btn_delivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DeliveryActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void showPriceInputDialog() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.ThemeOverlay_App_BottomSheetDialog);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_layout);
        bottomSheetDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        bottomSheetDialog.setCancelable(false);

        EditText txt_price = (EditText) bottomSheetDialog.findViewById(R.id.txt_bottom_price);
        TextView btn_bottom_cancel = (TextView) bottomSheetDialog.findViewById(R.id.btn_bottom_cancel);
        btn_bottom_ok = (TextView) bottomSheetDialog.findViewById(R.id.btn_bottom_ok);
        txt_distance = (TextView) bottomSheetDialog.findViewById(R.id.txt_distance);
        Button btn_payment_cash = (Button) bottomSheetDialog.findViewById(R.id.btn_payment_cash);

        txt_price.requestFocus();
        AddConstantTextInEditText(txt_price, " MXN");
        InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        btn_payment_cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.getInstance().setPay_type("cash");
            }
        });

        btn_bottom_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(txt_price.getApplicationWindowToken(),0);
                String total_price = txt_price.getText().toString().replace(" MXN", "");
                if(txt_price.getText().toString().length() == 0 || total_price.equals("")) {
                    onShowAlert(getResources().getString(R.string.string_offer));
                    return;
                }

                offer_price = Math.round(Integer.parseInt(txt_price.getText().toString().replace(" MXN", "")));

                trip_money.setText(String.valueOf(offer_price) + " MXN");

                if(distance > 5) {
                    min_price = (int) Math.round(distance * km_price);
                }
                if(offer_price < min_price) {
                    onShowAlert(getResources().getString(R.string.string_minimum) + min_price);
                    return;
                }
                bottomSheetDialog.cancel();
                btn_bottom_ok.setVisibility(View.GONE);
            }
        });

        btn_bottom_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(txt_price.getApplicationWindowToken(),0);
                bottomSheetDialog.cancel();
                btn_bottom_ok.setVisibility(View.GONE);
            }
        });
//        bottomSheetDialog.getWindow().setDimAmount(0);
        bottomSheetDialog.show();

        getCitysInfo();
    }

    private void AddConstantTextInEditText(EditText edt, String text) {

        Selection.setSelection(edt.getText(), edt.getText().length());
        edt.setText(text);

        edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().endsWith(text)){
                    Selection.setSelection(edt.getText(), edt.getText().length());
                    edt.setText(text);
                }
            }
        });
    }

    private void SearchDriver(){
        if(mTaxis.isEmpty()){
            onShowAlert(getResources().getString(R.string.no_taxi));
        }else{
            if(!priceway) {
                if(offertype == 0) {
                    if( offer_price == 0 ) {
                        onShowAlert(getResources().getString(R.string.string_offer));
                        return;
                    }

                    if(offer_price < min_price) {
                        onShowAlert(getResources().getString(R.string.string_minimum) + min_price);
                        return;
                    } else {
                        min_price = offer_price;
                    }
                    Common.getInstance().setPay_amount(min_price);
                }
            } else {
                min_price = offer_price;
            }
            Common.getInstance().setPay_amount(min_price);
            String comment = trip_comment.getText().toString();
            Common.getInstance().setLocations(locations);
            Common.getInstance().setComment(comment);

            Intent intent = new Intent(HomeActivity.this, FindDriverActivity.class);
            intent.putExtra("uuid", "null");
            startActivity(intent);
            finish();
        }
    }

    private void getCityInfo() {
        try {
            start_addresses = geocoder.getFromLocation(locations.get(0).latitude, locations.get(0).longitude, 1);
            String api_url = "";
            String start_city = start_addresses.get(0).getLocality();
            JsonObject json = new JsonObject();
            json.addProperty("city", start_city);
            api_url = "api/getCityInfo";
            Ion.with(this)
                    .load(Common.getInstance().getBaseURL()+api_url)
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (result != null) {
                                String res = result.get("status").getAsString();
                                if(res.equals("ok")) {
                                    String type = result.get("type").getAsString();
                                    if(type.equals("0")) {
                                        priceway = false;
                                        offertype = Integer.parseInt(result.get("offer_type").getAsString());
//                                        Common.getInstance().setOfferType(offertype);
                                        Common.getInstance().setOfferType(offertype);
                                        if(offertype == 1) {
                                            trip_money.setVisibility(View.GONE);
                                            passengerOffer = false;
                                        }
                                    }
                                } else {

                                }
                            } else {
                            }
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getCitysInfo() {
        List<Address> start_addresses;
        List<Address> end_addresses;
        try {
            start_addresses = geocoder.getFromLocation(locations.get(0).latitude, locations.get(0).longitude, 1);
//            end_addresses = geocoder.getFromLocation(end_location.latitude, end_location.longitude, 1);
            boolean isSamecity = true;

            String start_city = start_addresses.get(0).getLocality();
            String end_city = "";
            for(int i = 1; i < locations.size(); i++) {
                end_addresses = geocoder.getFromLocation(locations.get(i).latitude, locations.get(i).longitude, 1);
                if(end_addresses == null)
                    continue;
                end_city = end_addresses.get(0).getLocality();
                if(end_city == null) {
                    continue;
                }
                if(!start_city.equals(end_city)) {
                    isSamecity = false;
                    break;
                }
            }

            String api_url = "";
            JsonObject json = new JsonObject();
            if(isSamecity) {
                json.addProperty("city", start_city);
                api_url = "api/getCityInfo";
            } else {
                api_url = "api/getPricePerKm";
            }
            _start_confirm.setEnabled(false);
            _start_confirm.setTextColor(getResources().getColor(R.color.colorWhite));
            Ion.with(this)
                    .load(Common.getInstance().getBaseURL()+api_url)
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (result != null) {
                                String res = result.get("status").getAsString();
                                if(res.equals("ok")) {
                                    String type = result.get("type").getAsString();
                                    min_price = Integer.parseInt(result.get("min").getAsString());
                                    km_price = Integer.parseInt(result.get("km_price").getAsString());
                                    if(type.equals("0")) {
                                        priceway = false;
                                        offertype = Integer.parseInt(result.get("offer_type").getAsString());
                                        Common.getInstance().setOfferType(offertype);
                                        txt_distance.setText(getResources().getString(R.string.string_total_distance) + ": " + String.format("%.2f", distance) + "km");
                                        btn_bottom_ok.setVisibility(View.VISIBLE);
                                        _start_confirm.setBackgroundResource(R.drawable.ripple_effect_black);
                                        _start_confirm.setTextColor(getResources().getColor(R.color.colorWhite));
                                        _start_confirm.setEnabled(true);
//                                        if(offertype == 0) {
//                                            layout_set_offer.setVisibility(View.VISIBLE);
//                                        } else {
//                                            layout_set_offer.setVisibility(View.GONE);
//                                        }
                                    } else {
                                        priceway = true;
                                        txt_distance.setText(getResources().getString(R.string.string_total_distance) + ": " + distance + "km");
                                        btn_bottom_ok.setVisibility(View.VISIBLE);
                                        _start_confirm.setBackgroundResource(R.drawable.ripple_effect_pink);
                                        _start_confirm.setTextColor(getResources().getColor(R.color.colorWhite));
                                        _start_confirm.setEnabled(true);
                                        trip_money.setVisibility(View.GONE);
                                    }
                                } else {

                                }
                            } else {
                            }
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
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
//                mapFragment.getMapAsync(HomeActivity.this);
                for(int i = 0; i < taxi_markers.size(); i++) {
                    taxi_markers.get(i).remove();
                }
                taxi_markers.clear();
                for(Taxi oneTaxi : mTaxis){

                    mMyLocation.setLatitude(my_location.latitude);
                    mMyLocation.setLongitude(my_location.longitude);
                    mTaxiLocation.setLatitude(oneTaxi.getmTaxiLocation().latitude);
                    mTaxiLocation.setLongitude(oneTaxi.getmTaxiLocation().longitude);
                    if(mMyLocation.distanceTo(mTaxiLocation) < 5000){
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(oneTaxi.getmTaxiLocation());
                        if(oneTaxi.getmType().equals("0")) {
                            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                        } else {
                            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(motoMarker));
                        }

                        Marker mark = GMap.addMarker(markerOptions);
                        taxi_markers.add(mark);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
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

        mRef = mDatabase.getReference("user/" + mAuth.getUid() + "/phonenumber");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String phonenumber = dataSnapshot.getValue(String.class);
                if (phonenumber != null && phonenumber != "") {
                    Common.getInstance().setPhonenumber(phonenumber);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void onLocaAvatar(String url) {
        Picasso.get().load(Common.getInstance().getBaseURL() + "backend/" + url).into(ivHeaderPhoto);
        getAvatarView(HomeActivity.this);
        Picasso.get().load(Common.getInstance().getBaseURL() + "backend/" + url).into(user_dp, new Callback.EmptyCallback() {
            @Override public void onSuccess() {
                if(GMap != null) {
                    start_markerOptions = new MarkerOptions();
                    createCustomMarker();
                    Bitmap bitmap = Common.getInstance().getBitmap();
                    start_markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                    img_start_location.setImageBitmap(bitmap);
                    img_start_location.setPadding(0, 0, 0, bitmap.getHeight());
                    GMap.moveCamera(CameraUpdateFactory.newLatLngZoom(my_location, 14));
                    onSetMarker();
                }

            }
        });
    }

    private void onSetMenuEvent() {
        btn_go_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(HomeActivity.this, UserProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btn_user_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent=new Intent(HomeActivity.this, HomeActivity.class);
//                startActivity(intent);
//                finish();
            }
        });
        btn_user_privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(HomeActivity.this, ProtectionActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btn_user_ride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(HomeActivity.this, TripHistoryActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btn_user_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(HomeActivity.this, UserSettingActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btn_user_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        btn_user_support.setOnClickListener(new View.OnClickListener() {
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
        mRef = mDatabase.getReference("user/"+ mAuth.getUid() + "/enable");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String driver_enabled = dataSnapshot.getValue(String.class);
                if(driver_enabled.equals("yes")){
                    mDatabase.getReference("user/"+mAuth.getUid()+"/type").setValue("driver");
                    mDatabase.getReference("user/"+mAuth.getUid()+"/join").setValue("on");
                    mDatabase.getReference("user/"+mAuth.getUid()+"/phonetoken").setValue(Common.getInstance().getPhone_token());
//                    mDatabase.getReference("user/"+mAuth.getUid()+"/phonenumber").setValue(phone_token);
                    Intent intent = new Intent(HomeActivity.this, OffersActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    onCheckVerify();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void onCheckVerify() {
        JsonObject json = new JsonObject();
        json.addProperty("uid", mAuth.getUid());
        try {
            Ion.with(this)
                    .load(Common.getInstance().getBaseURL()+"api/checkverify")
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (result != null) {
                                String res = result.get("status").getAsString();
                                if (res.equals("ok")) {
                                    int id_request = result.get("id_request").getAsInt();
                                    if(id_request == 0) {
                                        Intent intent = new Intent(HomeActivity.this, VerifyActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else if(id_request == 1) {
                                        onShowAlert(getResources().getString(R.string.review_verify));
                                    }

                                } else {
                                }
                            } else {
                            }
                        }
                    });
        }catch(Exception e){
        }
    }

    private void onShowAlert(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg)
                .setPositiveButton(getResources().getString(R.string.string_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        // Create the AlertDialog object and return it
        builder.create();
        builder.show();
    }

    @Override
    public void onCameraIdle() {
        LatLng location = GMap.getCameraPosition().target;
//        selectLocation = 1;

        if(!mMapIsTouched || selectLocation == 0) {
            return;
        }
        if(zoomLevel == GMap.getCameraPosition().zoom) {
            my_location = location;
        } else {
            if(my_location.equals(location)) {
                return;
            }
        }
        mMapIsTouched = false;
        locations.set(0, my_location);
        try {
            start_addresses = geocoder.getFromLocation(my_location.latitude, my_location.longitude, 1);
            String start_city = start_addresses.get(0).getLocality();
            String throughfare = start_addresses.get(0).getThoroughfare();
            _start_location.setText(throughfare + ", " + start_city);
            txt_location_name.setText(throughfare + ", " + start_city);
            txt_location_name.setVisibility(View.VISIBLE);
            drawRoute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCameraMoveCanceled() {
    }
}