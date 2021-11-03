package com.mobiledevteam.kuiki;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
//import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;
import com.tylersuehr.bubbles.BubbleLayout;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import co.intentservice.chatui.models.ChatMessage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

import static java.util.Locale.getDefault;

public class MainDriverActivity extends AppCompatActivity implements LocationListener, OnMapReadyCallback {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private DatabaseReference mBookRef;
    private DatabaseReference oppMsgRef;
    private LocationManager locationmanager;
    private LatLng myLocation;
    private LatLng targetLocation;
    private SupportMapFragment mapFragment;
    private String joinStatus;
    private Geocoder geocoder;
    private List<Address> addresses;
    private ChildEventListener _childListener;
    private int unread = 0;

    private LinearLayout _layoutConfirm;
    private TextView _startAddress;
    private TextView _endAddress;
    private Button _confirm;
    private Button _cancel;
    private Button _phoneCall;
    private Button _whatsppCall;
    private ImageView _img_avatar;
    private TextView _txt_name;
    private Button more;
    private Button plus;
    private Button minus;
    private Button offer1;
    private Button offer2;
    private Button offer3;
    private LinearLayout popup_other_offers;
    private LinearLayout layout_offer_input;
    private LinearLayout layout_passenger_price;
    private LinearLayout layout_driver_price;
    private Button moreRate;
    private TextView txt_setting_price;
    private LinearLayout layout_price_list;
    private LinearLayout layout_time;
    private Button btn_first_time;
    private Button btn_second_time;
    private Button btn_third_time;
    private Button btn_forth_time;
    private SwitchButton switchButton;
    private Button btn_navigation;
    private Button btn_driver_panic;
    private LinearLayout layout_top_cancel;
    private TextView btn_top_cancel;
    private LinearLayout layout_arrived;
    private LinearLayout layout_passenger_arrived;
    private LinearLayout layout_driver_timer;
    private TextView txt_driver_timer;
    private BubbleLayout avatar_bubble;
    private TextView txt_trip_comment;
    private TextView txt_user_review;
    private TextView txt_counter_badge;

    private LatLng startLocation;
    private LatLng endLocation;
    private String phoneNumber;
    private int price;
    private String bookingStatus;
    private String ride_uuid;
    private String remove_status="yes";
    private String avatar_link;
    private String name;
    private String paytype;
    private String passenger_offer;
    private int offer_type;
    private int driver_timer;
    private CountDownTimer countDownTimer;
    private boolean drawedRoute = false;
    private ArrayList<LatLng> locations;

    ImageView ivHeaderPhoto;
    TextView ivHeadName;
    private DrawerLayout mDrawer;
    private NavigationView nvDrawer;
    LinearLayout btn_profile;
    LinearLayout btn_offers;
    LinearLayout btn_setting;
    LinearLayout btn_safety;
    LinearLayout btn_help;
    LinearLayout btn_support;
    Button btn_mode;
    ProgressBar timer;

    private GoogleMap mMap;
    private Bitmap start_smallMarker;
    private Bitmap end_smallMarker;
    private Marker start_marker;
    private Marker end_marker;
    private Bitmap car_marker;
    private Button btn_menu;

    private int height = 120;
    private int width = 120;
    private int custom_price;
    int first;
    int second;
    int third;
    int forth;
    private boolean isSelectedPrice = false;
    private boolean isSentOffer = false;
    private int reason = 0;
    private String reason_comment = "";
    private boolean sendNotification = false;
    private boolean isAccepted = false;

    String [] strPrice;
    String [] strArrival;
    ArrayList points = null;
    PolylineOptions lineOptions = null;
    String uniqueId;
    Thread thread;

    MarkerOptions start_markerOptions;
    MarkerOptions end_markerOptions;
    MarkerOptions car_markerOptions;
    private boolean isNew = true;

    private OkHttpClient httpClient = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_driver);
        Intent intent = getIntent();
        ride_uuid = intent.getStringExtra("id");
        String status = intent.getStringExtra("type");
        if(status.equals("new")) {
            isNew = true;
        } else {
            isNew = false;
        }

        geocoder = new Geocoder(this, getDefault());
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        Common.getInstance().setDriver_uid(mAuth.getUid());

        _layoutConfirm = (LinearLayout)findViewById(R.id.layout_confirm);
        _layoutConfirm.setVisibility(View.GONE);
        _startAddress = (TextView)findViewById(R.id.popup_start_address);
        _endAddress = (TextView) findViewById(R.id.popup_end_address);
        _confirm = (Button) findViewById(R.id.btn_booking);
        _cancel = (Button) findViewById(R.id.btn_driver_arrive);
        _phoneCall = (Button) findViewById(R.id.btn_call);
        _whatsppCall = (Button) findViewById(R.id.btn_whatsapp);
        _img_avatar = (ImageView) findViewById(R.id.img_driver_avatar);
        _txt_name = (TextView) findViewById(R.id.txt_popup_name);
        more = (Button) findViewById(R.id.btn_new_offer);
        plus = (Button) findViewById(R.id.btn_offer_plus);
        minus = (Button) findViewById(R.id.btn_offer_minus);
        offer1 = (Button) findViewById(R.id.btn_popup_offer1);
        offer2 = (Button) findViewById(R.id.btn_popup_offer2);
        offer3 = (Button) findViewById(R.id.btn_popup_offer3);
        popup_other_offers = (LinearLayout) findViewById(R.id.popup_other_offers);
        layout_offer_input = (LinearLayout) findViewById(R.id.layout_offer_input);
        layout_passenger_price = (LinearLayout) findViewById(R.id.layout_passenger_price);
        layout_driver_price = (LinearLayout) findViewById(R.id.layout_driver_price);
        moreRate = (Button) findViewById(R.id.btn_more_price);
        txt_setting_price = (TextView) findViewById(R.id.txt_setting_price);
        layout_price_list = (LinearLayout) findViewById(R.id.layout_price_list);
        layout_time = (LinearLayout) findViewById(R.id.layout_driver_time);
        btn_first_time = (Button)findViewById(R.id.btn_first_time);
        btn_second_time = (Button)findViewById(R.id.btn_second_time);
        btn_third_time = (Button)findViewById(R.id.btn_third_time);
        btn_forth_time = (Button)findViewById(R.id.btn_forth_time);
        timer = (ProgressBar) findViewById(R.id.progressBar);
        btn_navigation = (Button) findViewById(R.id.btn_navigation);
        btn_driver_panic = (Button) findViewById(R.id.btn_driver_panic);
        btn_menu = (Button) findViewById(R.id.btn_menu);
        layout_top_cancel = (LinearLayout) findViewById(R.id.layout_top_cancel);
        btn_top_cancel = (TextView) findViewById(R.id.btn_top_cancel);
        layout_arrived = (LinearLayout) findViewById(R.id.layout_arrived);
        layout_passenger_arrived = (LinearLayout) findViewById(R.id.layout_passenger_arrived);
        layout_driver_timer = (LinearLayout) findViewById(R.id.layout_driver_timer);
        txt_driver_timer = (TextView) findViewById(R.id.txt_driver_timer);
        avatar_bubble = (BubbleLayout) findViewById(R.id.avatar_bubble);
        txt_trip_comment = (TextView)findViewById(R.id.txt_trip_comment);
        txt_user_review = (TextView) findViewById(R.id.txt_user_review);
        txt_counter_badge = (TextView) findViewById(R.id.txt_counter_badge);
        locations = Common.getInstance().getLocations();
        startLocation = locations.get(0);


        btn_top_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCancelTrip();
            }
        });

        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawer.openDrawer(nvDrawer);
            }
        });

        _phoneCall.setVisibility(View.GONE);
        _whatsppCall.setVisibility(View.GONE);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_driver_layout);
        nvDrawer = (NavigationView) findViewById(R.id.driverView);
        NavigationView navigationView = (NavigationView) findViewById(R.id.driverView);
        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_driver);
        ivHeaderPhoto = headerLayout.findViewById(R.id.avatar);
        ivHeadName = headerLayout.findViewById(R.id.user_name);
        btn_profile = (LinearLayout) headerLayout.findViewById(R.id.btn_go_profile);
        btn_offers = (LinearLayout) headerLayout.findViewById(R.id.btn_user_trip);
        btn_safety = (LinearLayout) headerLayout.findViewById(R.id.btn_driver_privacy);
        btn_setting = (LinearLayout) headerLayout.findViewById(R.id.btn_driver_setting);
        btn_help = (LinearLayout) headerLayout.findViewById(R.id.btn_driver_help);
        btn_support = (LinearLayout) headerLayout.findViewById(R.id.btn_driver_support);
        btn_mode = (Button)findViewById(R.id.btn_passenger_mode);

        _phoneCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(MainDriverActivity.this,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(MainDriverActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 1);

                }else{
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
                    startActivity(intent);
                }
            }
        });
        _whatsppCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DriverChatActivity.class);
                intent.putExtra("trip", "yes");
                startActivityForResult(intent, 0);
//                String url = "https://api.whatsapp.com/send?phone=$" + phoneNumber;
//                try {
//                    PackageManager packageManager = getApplicationContext().getPackageManager();
//                    packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
//                    Intent i = new Intent(Intent.ACTION_VIEW);
//                    i.setData(Uri.parse(url));
//                    startActivity(i);
//                } catch (PackageManager.NameNotFoundException e) {
//                    Toast.makeText(MainDriverActivity.this, getResources().getString(R.string.string_no_whatsapp), Toast.LENGTH_LONG ).show();
//                }
            }
        });
        _cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bookingStatus.equals("waiting")) {
                    if(!isSentOffer) {
                        if(!isSelectedPrice) {
                            Intent intent1 = new Intent(MainDriverActivity.this, OffersActivity.class);
                            startActivity(intent1);
                            finish();
                        } else {
                            isSelectedPrice = false;
                            layout_time.setVisibility(View.GONE);
                            _cancel.setText(getResources().getString(R.string.string_close));
                            if(offer_type == 0) {
                                layout_passenger_price.setVisibility(View.VISIBLE);
                                layout_driver_price.setVisibility(View.GONE);
                            } else {
                                layout_passenger_price.setVisibility(View.GONE);
                                layout_driver_price.setVisibility(View.VISIBLE);
                            }
                        }
                    } else {
//                        Thread.currentThread().interrupt();
//                        thread.stop();
                        removeOffer();
                    }
                } else {
                    bookingRide();
                }
            }
        });

        offer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int offer_price = (int)Math.round(price + price * 0.1);
                setOfferPrice(offer_price);
            }
        });

        offer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int offer_price = (int)Math.round(price + price * 0.15);
                setOfferPrice(offer_price);
            }
        });

        offer3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int offer_price = (int)Math.round(price + price * 0.2);
                setOfferPrice(offer_price);
            }
        });

        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setOfferPrice(custom_price);
            }
        });

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                custom_price = custom_price + 5;
                more.setText(String.valueOf(custom_price) + "MXN");
            }
        });

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(custom_price - 5 >= price) {
                    custom_price = custom_price - 5;
                    more.setText(String.valueOf(custom_price) + "MXN");
                }
            }
        });

        moreRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onShowMoreRate();
            }
        });

        _confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bookingStatus.equals("waiting")) {
                    int offer_price = price;
                    setOfferPrice(offer_price);
                } else {
                    bookingRide();
                }
            }
        });

        btn_navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String waypoints = "";
                for(int i=0;i<locations.size();i++){
                    LatLng point  = (LatLng) locations.get(i);
                    waypoints += point.latitude + "," + point.longitude + "/";
                }
                Uri gmmIntentUri = Uri.parse("https://www.google.co.in/maps/dir/" + waypoints);
                final Intent intent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.driveabout.app.NavigationActivity");
                startActivity(intent);
            }
        });
        btn_driver_panic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(MainDriverActivity.this,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(MainDriverActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 1);

                }else{
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:911"));
                    startActivity(intent);
                }
            }
        });

        locationmanager = (LocationManager) getSystemService(LOCATION_SERVICE);
        String provider = locationmanager.getBestProvider(new Criteria(), true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 30, this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        BitmapDrawable bitmapdraw1 = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_map_start);
        Bitmap b1 = bitmapdraw1.getBitmap();
        start_smallMarker = Bitmap.createScaledBitmap(b1, width, height, false);
        BitmapDrawable bitmapdraw2 = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_map_end);
        Bitmap b2 = bitmapdraw2.getBitmap();
        end_smallMarker = Bitmap.createScaledBitmap(b2, width, height, false);

        start_markerOptions = new MarkerOptions();
        start_markerOptions.icon(BitmapDescriptorFactory.fromBitmap(start_smallMarker));
        end_markerOptions = new MarkerOptions();
        end_markerOptions.icon(BitmapDescriptorFactory.fromBitmap(end_smallMarker));

        BitmapDrawable bitmapDrawable = (BitmapDrawable)getResources().getDrawable(R.drawable.ic_car_front);
        Bitmap b = bitmapDrawable.getBitmap();
        car_marker = Bitmap.createScaledBitmap(b, 100, 100, false);

        onGetTripValue();
        onRemoveTripPanel();
        onGetUserInfo();
        onSetMenuEvent();
        onGetTime();
        setSwitch();
        onSetTripInfo();
    }

    private void onSetTripInfo() {

        _txt_name.setText(name);
        _layoutConfirm.setVisibility(View.VISIBLE);

        targetLocation = startLocation;
        String start_address = getLocationAddress(startLocation);

        String destName = "";
        List<Address> end_addresses;
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

        try {
            addresses = geocoder.getFromLocation(startLocation.latitude, startLocation.longitude, 1);
            start_address = addresses.get(0).getThoroughfare() + ", " + addresses.get(0).getLocality();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        drawRoute();

//                    start_marker = mMap.addMarker((start_markerOptions).title("Title"));
//                    end_marker = mMap.addMarker((end_markerOptions).title("Title"));

        Log.d("Data2 ready::", "Data2 ready");

        _startAddress.setText(start_address);
        Common.getInstance().setStart_address(start_address);
        _endAddress.setText(destName);
        Common.getInstance().setEnd_address(destName);
    }

    private void setProgressValue(final int progress) {

        // set the progress
        timer.setProgress(progress);
        int val = timer.getProgress();
        // thread is used to change the progress value
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep( 200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(val == 0) {
//                    Thread.currentThread().interrupt();
                    if(!isAccepted) {
                        removeOffer();
                    }
//                    thread.stop();
                } else {
                    setProgressValue(progress - 1);
                }
            }
        });
        thread.start();
    }

    private void removeOffer() {
        mDatabase.getReference("offer/"+uniqueId).removeValue();
    }

    private void setSwitch() {
        switchButton = (SwitchButton) findViewById(R.id.switch_button);
        switchButton.setVisibility(View.VISIBLE);
        switchButton.setChecked(Common.getInstance().getJoin());
        switchButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if(isChecked) {
                    mDatabase.getReference("user/"+mAuth.getUid()+"/join").setValue("on");
                } else {
                    mDatabase.getReference("user/"+mAuth.getUid()+"/join").setValue("off");
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MainDriverActivity.this, OffersActivity.class);
        startActivity(intent);
        finish();
    }

    private void onGetTripValue() {
        mRef = mDatabase.getReference("ride/" + ride_uuid);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                onGetStartEndLocation(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void onGetTime() {
        JsonObject json = new JsonObject();
        try {
            Ion.with(this)
                    .load(Common.getInstance().getBaseURL()+"api/gettimer")
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (result != null) {
                                String res = result.get("status").getAsString();
                                if (res.equals("ok")) {
                                    first = Integer.parseInt(result.get("first").getAsString());
                                    second = Integer.parseInt(result.get("second").getAsString());
                                    third = Integer.parseInt(result.get("third").getAsString());
                                    forth = Integer.parseInt(result.get("forth").getAsString());
                                    btn_first_time.setText(first + " min");
                                    btn_second_time.setText(second + " min");
                                    btn_third_time.setText(third + " min");
                                    btn_forth_time.setText(forth + " min");
                                } else {
                                }
                            } else {
                            }
                        }
                    });
        }catch(Exception e){
        }
    }

    private void onRemoveTripPanel() {
        mBookRef= mDatabase.getReference("ride/");
        mBookRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                if (ride_uuid.equals(snapshot.getKey())){
                    _layoutConfirm.setVisibility(View.GONE);
                }
                if(remove_status.equals("yes")){
                    onShowAlert(getResources().getString(R.string.cancel_ride));
                }
                if(mMap != null) {
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(myLocation));
                    remove_status = "yes";
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void onGetStartEndLocation(DataSnapshot ds) {
            bookingStatus = ds.child("status").getValue(String.class);
            if (bookingStatus != null) {
                paytype = ds.child("paytype").getValue(String.class);
                phoneNumber = ds.child("passengernumber").getValue(String.class);
                avatar_link = ds.child("avatar").getValue(String.class);
                offer_type = ds.child("offer_type").getValue(Integer.class);
                String user_token = ds.child("phone_token").getValue(String.class);
                String comment = ds.child("comment").getValue(String.class);
                Common.getInstance().setUserPhone_token(user_token);
                String passenger_uid = ds.child("passenger").getValue(String.class);
                String review = ds.child("review").getValue(String.class);
                Common.getInstance().setUser_uid(passenger_uid);
                onCheckUnreadChat();
                if (offer_type == 1) {
                    onGetFixedPrice();
                }

                try {
                    passenger_offer = ds.child("passenger_offer").getValue(String.class);
                } catch (Exception e) {
                    passenger_offer = "no";
                    layout_passenger_price.setVisibility(View.VISIBLE);
                    popup_other_offers.setVisibility(View.VISIBLE);
                    layout_offer_input.setVisibility(View.VISIBLE);
                }
                txt_trip_comment.setText(comment);
                startLocation = locations.get(0);
                targetLocation = startLocation;
                name = ds.child("passenger_name").getValue(String.class);
                price = ds.child("price").getValue(Integer.class);
                custom_price = ds.child("price").getValue(Integer.class);
                txt_user_review.setText(review);

                if(name.split("\\w+").length>1){

                    String lastName = name.substring(name.lastIndexOf(" ")+1);
                    String firstName = name.substring(0, name.lastIndexOf(' '));
                    lastName = lastName.substring(0, 1) + ".";
                    name = firstName + " " + lastName;
                    _txt_name.setText(name);
                }
                else{
                    _txt_name.setText(name);
                }
                _txt_name.setText(name);
                Picasso.get().load(Common.getInstance().getBaseURL() + "backend/" + avatar_link).into(_img_avatar);
//                custom_price = price;
                onProgress();
            }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.clear();
        car_markerOptions = new MarkerOptions();
        car_markerOptions.icon(BitmapDescriptorFactory.fromBitmap(car_marker));
        car_markerOptions.position(myLocation);
        googleMap.addMarker(car_markerOptions);

        if(locations.size() > 1) {
//            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(startLocation,14));

            if(!drawedRoute) {
                onDrawRoute();
            }
        } else {
//            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation,14));
        }
    }

    private void setOfferPrice(int send_price) {
        isSelectedPrice = true;
        _cancel.setText(getResources().getString(R.string.string_cancel));
        price = send_price;
        layout_time.setVisibility(View.VISIBLE);
        layout_passenger_price.setVisibility(View.GONE);
        layout_driver_price.setVisibility(View.GONE);
        sendPriceTime();
    }

    public void onPickTime(View view) {
        int id = view.getId();
        int time;
        switch (id) {
            case R.id.btn_first_time:
                view.setBackground(getResources().getDrawable(R.drawable.ripple_effect_green));
                findViewById(R.id.btn_second_time).setBackground(getResources().getDrawable(R.drawable.ripple_effect_black));
                findViewById(R.id.btn_third_time).setBackground(getResources().getDrawable(R.drawable.ripple_effect_black));
                findViewById(R.id.btn_forth_time).setBackground(getResources().getDrawable(R.drawable.ripple_effect_black));
                time = first;
                break;
            case R.id.btn_second_time:
                view.setBackground(getResources().getDrawable(R.drawable.ripple_effect_green));
                findViewById(R.id.btn_first_time).setBackground(getResources().getDrawable(R.drawable.ripple_effect_black));
                findViewById(R.id.btn_third_time).setBackground(getResources().getDrawable(R.drawable.ripple_effect_black));
                findViewById(R.id.btn_forth_time).setBackground(getResources().getDrawable(R.drawable.ripple_effect_black));
                time = second;
                break;
            case R.id.btn_third_time:
                view.setBackground(getResources().getDrawable(R.drawable.ripple_effect_green));
                findViewById(R.id.btn_first_time).setBackground(getResources().getDrawable(R.drawable.ripple_effect_black));
                findViewById(R.id.btn_second_time).setBackground(getResources().getDrawable(R.drawable.ripple_effect_black));
                findViewById(R.id.btn_forth_time).setBackground(getResources().getDrawable(R.drawable.ripple_effect_black));
                time = third;
                break;
            default:
                view.setBackground(getResources().getDrawable(R.drawable.ripple_effect_green));
                findViewById(R.id.btn_first_time).setBackground(getResources().getDrawable(R.drawable.ripple_effect_black));
                findViewById(R.id.btn_second_time).setBackground(getResources().getDrawable(R.drawable.ripple_effect_black));
                findViewById(R.id.btn_third_time).setBackground(getResources().getDrawable(R.drawable.ripple_effect_black));
                time = forth;
                break;
        }
        layout_time.setVisibility(View.GONE);
//        mDatabase.getReference("ride/"+ride_uuid+"/passenger_offer").setValue(price);
//        mDatabase.getReference("ride/"+ride_uuid+"/arrival_time").setValue(time);

        uniqueId = UUID.randomUUID().toString();

        Location _start_location = new Location("start");
        _start_location.setLatitude(myLocation.latitude);
        _start_location.setLongitude(myLocation.longitude);
        Location _end_location = new Location("end");
        _end_location.setLatitude(startLocation.latitude);
        _end_location.setLongitude(startLocation.longitude);
        int distance_value = Math.round(_start_location.distanceTo(_end_location));

        mDatabase.getReference("offer/"+uniqueId+"/driver").setValue(mAuth.getUid());
        mDatabase.getReference("offer/"+uniqueId+"/price").setValue(String.valueOf(price));
        mDatabase.getReference("offer/"+uniqueId+"/time").setValue(String.valueOf(time));
        mDatabase.getReference("offer/"+uniqueId+"/driver_name").setValue(Common.getInstance().getUserName());
        mDatabase.getReference("offer/"+uniqueId+"/driver_avatar").setValue(Common.getInstance().getAvatar());
        mDatabase.getReference("offer/"+uniqueId+"/driver_brand").setValue(Common.getInstance().getBrand());
        mDatabase.getReference("offer/"+uniqueId+"/driver_phone").setValue(Common.getInstance().getPhone_token());
        mDatabase.getReference("offer/"+uniqueId+"/distance").setValue(String.valueOf(distance_value));
        mDatabase.getReference("offer/"+uniqueId+"/driverphone").setValue(Common.getInstance().getPhonenumber());
        mDatabase.getReference("offer/"+uniqueId+"/ride").setValue(ride_uuid);
        mDatabase.getReference("offer/"+uniqueId+"/review").setValue(Common.getInstance().getReview());
        mDatabase.getReference("offer/"+uniqueId+"/status").setValue("waiting");

        driver_timer = time;

        timer.setVisibility(View.VISIBLE);
        setProgressValue(100);
        isSentOffer = true;
        checkOfferStatus();
    }

    private void sendNotification(String content){
        Map<String,Object> payMap = new HashMap<>();
        Map<String,Object> itemMap = new HashMap<>();
        String token = Common.getInstance().getUserPhone_token();
        payMap.put("to",Common.getInstance().getUserPhone_token());
        itemMap.put("body",content );
        itemMap.put("Title","KUIKI");
        payMap.put("notification",itemMap);
        String json = new Gson().toJson(payMap);
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(mediaType, json);
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url("https://fcm.googleapis.com/fcm/send")
                .post(body)
                .addHeader("Authorization", "key=AAAAu_pgIBk:APA91bFZRT5Yw52C6TTffiY6yoWUTtuQyfFZiqfK5lj_TfK1DTGmOvGRc_PWGW1DQ-vh-pZsw_NFk8m7hsBBm7jUjET3phUnXzSIIXeHiLaRmUv0gLusC2o0WzMyN4lkRnQg_7eZ9ZMk")
                .addHeader("Content-Type", "application/json")
                .build();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("error:", e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                Log.d("response:", response.toString());
            }
        });
    }

    private void onShowOtherOffers() {

    }

    private void checkOfferStatus() {
        mRef = mDatabase.getReference("offer");
        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot ds, @Nullable String s) {
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot ds, @Nullable String previousChildName) {
                String key = ds.getKey();
                String status = ds.child("status").getValue(String.class);
                if(key.equals(uniqueId) && status.equals("accepted")) {
                    timer.setVisibility(View.GONE);
//                    mDatabase.getReference("offer/"+uniqueId).removeValue();
//                    Thread.currentThread().interrupt();
                    isAccepted = true;
                    onCheckUnreadChat();
//                    thread.stop();
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String key = snapshot.getKey();
                String status = snapshot.child("status").getValue(String.class);
                if(key.equals(uniqueId) && !status.equals("accepted")) {
                    timer.setVisibility(View.GONE);
                    onShowAlert(getResources().getString(R.string.string_offer_rejected));
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void sendPriceTime() {
        layout_time.setVisibility(View.VISIBLE);
        layout_passenger_price.setVisibility(View.GONE);
        layout_driver_price.setVisibility(View.GONE);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        mDatabase.getReference("user/"+mAuth.getUid()+"/latitude").setValue(location.getLatitude());
        mDatabase.getReference("user/"+mAuth.getUid()+"/longitude").setValue(location.getLongitude());
        myLocation = new LatLng(location.getLatitude(), location.getLongitude());
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    private void onShowAlert(String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainDriverActivity.this);
                builder.setMessage(msg)
                        .setPositiveButton(getResources().getString(R.string.string_ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(MainDriverActivity.this, OffersActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                // Create the AlertDialog object and return it
                builder.create();
                if (!MainDriverActivity.this.isFinishing()) {
                    builder.show();
                }
            }
        });
    }

    private void onGetFixedPrice() {
        try {
        JsonObject json = new JsonObject();
        txt_setting_price.setVisibility(View.VISIBLE);
        layout_price_list.setVisibility(View.GONE);

        Ion.with(this)
                .load(Common.getInstance().getBaseURL()+"api/getfixedprice")
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (result != null) {
                            String res = result.get("status").getAsString();
                            if(res.equals("ok")) {
                                strPrice = result.get("arr_price").getAsString().split(",");
                                strArrival = result.get("arr_arrival").getAsString().split(",");
                                Button btn;
                                for(int i = 0; i < 8; i++) {
                                    switch (i) {
                                        case 0:
                                            btn = findViewById(R.id.btn_price1);
                                            break;
                                        case 1:
                                            btn = findViewById(R.id.btn_price2);
                                            break;
                                        case 2:
                                            btn = findViewById(R.id.btn_price3);
                                            break;
                                        case 3:
                                            btn = findViewById(R.id.btn_price4);
                                            break;
                                        case 4:
                                            btn = findViewById(R.id.btn_price5);
                                            break;
                                        case 5:
                                            btn = findViewById(R.id.btn_price6);
                                            break;
                                        case 6:
                                            btn = findViewById(R.id.btn_price7);
                                            break;
                                        default:
                                            btn = findViewById(R.id.btn_price8);
                                            break;
                                    }
                                    btn.setText(strPrice[i]);
                                }
                                txt_setting_price.setVisibility(View.GONE);
                                layout_price_list.setVisibility(View.VISIBLE);
                                if(mMap != null && !drawedRoute) {
                                    onDrawRoute();
                                }
                            } else {

                            }
                        } else {
                        }
                    }
                });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onDrawRoute() {
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for(int i = 0; i < locations.size(); i++) {
                    builder.include(locations.get(i));
                }
                LatLngBounds bounds = builder.build();
                int padding = 100; // offset from edges of the map in pixels
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                mMap.animateCamera(cu);
                drawedRoute = true;
            }
        },200);
        drawRoute();
    }

    private void onProgress() {
        if (bookingStatus.equals("waiting")){
            _confirm.setBackgroundResource(R.drawable.ripple_effect_black);
            if(offer_type == 0) {
                layout_passenger_price.setVisibility(View.VISIBLE);
                layout_driver_price.setVisibility(View.GONE);
                if(passenger_offer.equals("yes")) {
                    _confirm.setText(getResources().getString(R.string.string_accept) + " $"+String.valueOf(price) + "MXN");
                    more.setText(String.valueOf(price) + "MXN");
                    int offer1_price = (int)Math.round(price + price * 0.1);
                    int offer2_price = (int)Math.round(price + price * 0.15);
                    int offer3_price = (int)Math.round(price + price * 0.2);
                    offer1.setText(offer1_price + "MXN");
                    offer2.setText(offer2_price + "MXN");
                    offer3.setText(offer3_price + "MXN");
                    _confirm.setEnabled(true);
                    layout_passenger_price.setVisibility(View.VISIBLE);
                    popup_other_offers.setVisibility(View.VISIBLE);
                    layout_offer_input.setVisibility(View.VISIBLE);
                    if(mMap != null && !drawedRoute) {
                        onDrawRoute();
                    }
                } else {
                    _confirm.setText(getResources().getString(R.string.string_waiting));
                    _confirm.setEnabled(false);
                    layout_passenger_price.setVisibility(View.VISIBLE);
                    popup_other_offers.setVisibility(View.GONE);
                    layout_offer_input.setVisibility(View.GONE);
                }
            } else {
                if(passenger_offer.equals("yes")) {
                    layout_passenger_price.setVisibility(View.GONE);
                    layout_driver_price.setVisibility(View.VISIBLE);
                } else {
                    layout_passenger_price.setVisibility(View.VISIBLE);
                    popup_other_offers.setVisibility(View.GONE);
                    layout_offer_input.setVisibility(View.GONE);
                    layout_driver_price.setVisibility(View.GONE);
                    _confirm.setText(getResources().getString(R.string.string_waiting));
                    _confirm.setEnabled(false);
                }
            }

            _phoneCall.setVisibility(View.GONE);
            _whatsppCall.setVisibility(View.GONE);
        }else if (bookingStatus.equals("accept")){
            btn_navigation.setVisibility(View.VISIBLE);
            btn_driver_panic.setVisibility(View.VISIBLE);
            layout_driver_timer.setVisibility(View.VISIBLE);
            onCountdownTimer();
            _cancel.setVisibility(View.VISIBLE);
            _cancel.setTextColor(getResources().getColor(R.color.colorWhite));
//            _confirm.setVisibility(View.VISIBLE);
            layout_top_cancel.setVisibility(View.VISIBLE);
            if(paytype.equals("cash")) {
                _cancel.setText(getResources().getString(R.string.string_pickup));
                _cancel.setEnabled(true);
            } else {
                _cancel.setText(getResources().getString(R.string.string_waiting_payment));
                _cancel.setEnabled(false);
            }
            if(passenger_offer.equals("no")) {
//                onShowAlert(getResources().getString(R.string.passenger_accept));
            }
            _phoneCall.setVisibility(View.VISIBLE);
            _whatsppCall.setVisibility(View.VISIBLE);
            layout_passenger_price.setVisibility(View.GONE);
            layout_driver_price.setVisibility(View.GONE);
            layout_offer_input.setVisibility(View.GONE);
            popup_other_offers.setVisibility(View.GONE);
            _cancel.setBackgroundResource(R.drawable.ripple_effect_black);
            if(!sendNotification) {
                String msg = String.format(getResources().getString(R.string.string_notify_passenger_accept), String.valueOf(driver_timer));
                sendNotification(msg);
                sendNotification = true;
            }
        }else if (bookingStatus.equals("pickup")){
            layout_arrived.setVisibility(View.VISIBLE);
            layout_driver_timer.setVisibility(View.GONE);
            if(countDownTimer != null) {
                countDownTimer.cancel();
            }
            _cancel.setText(getResources().getString(R.string.string_waiting_passenger));
            _cancel.setTextColor(getResources().getColor(R.color.colorWhite));
            _cancel.setEnabled(false);
            _endAddress.setText(Common.getInstance().getEnd_address());
            targetLocation = endLocation;
            _phoneCall.setVisibility(View.VISIBLE);
            _whatsppCall.setVisibility(View.VISIBLE);
            layout_passenger_price.setVisibility(View.GONE);
            popup_other_offers.setVisibility(View.GONE);
            layout_offer_input.setVisibility(View.GONE);
            layout_driver_price.setVisibility(View.GONE);
            _cancel.setBackgroundResource(R.drawable.ripple_effect_black);
        }else if (bookingStatus.equals("pay")){
            layout_arrived.setVisibility(View.GONE);
            layout_passenger_arrived.setVisibility(View.VISIBLE);
            _cancel.setText(getResources().getString(R.string.string_end_trip));
            _cancel.setTextColor(getResources().getColor(R.color.colorWhite));
            _cancel.setBackground(getResources().getDrawable(R.drawable.ripple_effect_black));
            _cancel.setEnabled(true);
            _endAddress.setText(Common.getInstance().getEnd_address());
            targetLocation = endLocation;
            _phoneCall.setVisibility(View.VISIBLE);
            _whatsppCall.setVisibility(View.VISIBLE);
            layout_passenger_price.setVisibility(View.GONE);
            popup_other_offers.setVisibility(View.GONE);
            layout_offer_input.setVisibility(View.GONE);
            layout_driver_price.setVisibility(View.GONE);
            _cancel.setBackgroundResource(R.drawable.ripple_effect_black);
        }else if (bookingStatus.equals("paid")){
            layout_passenger_arrived.setVisibility(View.GONE);
            if(paytype.equals("cash")) {
                _cancel.setEnabled(true);
                _cancel.setText(getResources().getString(R.string.string_give_review));
                _endAddress.setText(Common.getInstance().getEnd_address());
                targetLocation = endLocation;
                _phoneCall.setVisibility(View.VISIBLE);
                _whatsppCall.setVisibility(View.VISIBLE);
            } else{
                _cancel.setText(getResources().getString(R.string.string_pickup));
                _cancel.setEnabled(true);
            }
            layout_passenger_price.setVisibility(View.GONE);
            popup_other_offers.setVisibility(View.GONE);
            layout_offer_input.setVisibility(View.GONE);
            layout_driver_price.setVisibility(View.GONE);
            _cancel.setBackgroundResource(R.drawable.ripple_effect_black);
            _cancel.setTextColor(getResources().getColor(R.color.colorWhite));
        } else if(bookingStatus.equals("process")) {
            layout_passenger_price.setVisibility(View.GONE);
            popup_other_offers.setVisibility(View.GONE);
            layout_offer_input.setVisibility(View.GONE);
            layout_driver_price.setVisibility(View.GONE);
            _cancel.setEnabled(true);
            _cancel.setText(getResources().getString(R.string.string_end_trip));
            _cancel.setTextColor(getResources().getColor(R.color.colorWhite));
        } else if(bookingStatus.equals("done")) {
            layout_passenger_price.setVisibility(View.GONE);
            popup_other_offers.setVisibility(View.GONE);
            layout_offer_input.setVisibility(View.GONE);
            layout_driver_price.setVisibility(View.GONE);
            _cancel.setEnabled(true);
            _cancel.setText(getResources().getString(R.string.string_give_review));
            _cancel.setTextColor(getResources().getColor(R.color.colorWhite));
        } else if (bookingStatus.equals("complete")){
            layout_passenger_price.setVisibility(View.GONE);
            popup_other_offers.setVisibility(View.GONE);
            layout_offer_input.setVisibility(View.GONE);
            layout_driver_price.setVisibility(View.GONE);
            _layoutConfirm.setVisibility(View.VISIBLE);
            mDatabase.getReference("offer/"+uniqueId).removeValue();
        }
    }

    private void onCountdownTimer() {
        txt_driver_timer.setText(String.format("%02d", driver_timer) + " : " + "00");
        countDownTimer = new CountDownTimer(driver_timer * 60 * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                String minute = String.format("%02d", (millisUntilFinished / 1000) / 60);
                String second =  String.format("%02d", (millisUntilFinished / 1000) % 60);
                txt_driver_timer.setText(minute + " : " + second);
            }

            public void onFinish() {
                if(isNew) {
                    txt_driver_timer.setText(getResources().getString(R.string.string_driver_late));
                } else {
                    txt_driver_timer.setVisibility(View.GONE);
                }
            }

        }.start();
    }

    private void bookingRide(){
        if (bookingStatus.equals("waiting")){
            mDatabase.getReference("user/"+mAuth.getUid()+"/join").setValue("off");
            mDatabase.getReference("ride/"+ride_uuid+"/status").setValue("accept");
        }else if (bookingStatus.equals("accept")){
            String notify_text = String.format(getResources().getString(R.string.string_notify_driver_arrive));
            sendNotification(notify_text);
            mDatabase.getReference("ride/"+ride_uuid+"/status").setValue("pickup");
        }else if (bookingStatus.equals("pickup")){
//            mDatabase.getReference("ride/"+uniqueId+"/status").setValue("pay");
        }else if (bookingStatus.equals("pay")){
            onShowEndTripAlert();
        }else if (bookingStatus.equals("paid")){
            if(paytype.equals("cash")) {
                mDatabase.getReference("user/"+mAuth.getUid()+"/join").setValue("on");
                mDatabase.getReference("ride/"+ride_uuid+"/status").setValue("complete");
                onRemoveChat();
                Intent intent=new Intent(MainDriverActivity.this, DriverReview.class);
                startActivity(intent);
                finish();
            } else {
                mDatabase.getReference("user/"+mAuth.getUid()+"/join").setValue("off");
                mDatabase.getReference("ride/"+ride_uuid+"/status").setValue("process");
            }
//            _layoutConfirm.setVisibility(View.GONE);
        } else if(bookingStatus.equals("process")) {
            onShowEndTripAlert();
        } else if(bookingStatus.equals("done")) {
            mDatabase.getReference("user/"+mAuth.getUid()+"/join").setValue("on");
            mDatabase.getReference("ride/"+ride_uuid+"/status").setValue("complete");
        } else if (bookingStatus.equals("complete")){
            mDatabase.getReference("user/"+mAuth.getUid()+"/join").setValue("on");
            _layoutConfirm.setVisibility(View.VISIBLE);
//            onSaveTriptoDB(1);
            onRemoveChat();
            Intent intent=new Intent(MainDriverActivity.this, DriverReview.class);
            startActivity(intent);
            finish();
        }
    }

    private void onShowEndTripAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainDriverActivity.this);
        builder.setMessage(getResources().getString(R.string.string_trip_complete_question))
                .setPositiveButton(getResources().getString(R.string.string_yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
//                        if(paytype.equals("cash")) {
//                            mDatabase.getReference("ride/"+ride_uuid+"/status").setValue("complete");
//                            mDatabase.getReference("user/"+mAuth.getUid()+"/join").setValue("on");
//                        } else {
//                            mDatabase.getReference("user/"+mAuth.getUid()+"/join").setValue("on");
//                            mDatabase.getReference("ride/"+ride_uuid+"/status").setValue("done");
//                        }
                        mDatabase.getReference("ride/"+ride_uuid+"/status").setValue("complete");
                        mDatabase.getReference("user/"+mAuth.getUid()+"/join").setValue("on");
                        onSaveTriptoDB(1);
                        onRemoveChat();
                        Intent intent=new Intent(MainDriverActivity.this, DriverReview.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.string_no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onInCompleteTrip();
                    }
                });
        builder.setCancelable(false);
        // Create the AlertDialog object and return it
        builder.create();
        if (!MainDriverActivity.this.isFinishing()) {
            builder.show();
        }
    }

    private void onInCompleteTrip() {
        LayoutInflater factory = LayoutInflater.from(MainDriverActivity.this);
        final View mView = factory.inflate(R.layout.incomplete_reason, null);
        RadioButton reason1 = (RadioButton) mView.findViewById(R.id.btn_reason1);
        RadioButton reason2 = (RadioButton) mView.findViewById(R.id.btn_reason2);
        RadioButton reason3 = (RadioButton) mView.findViewById(R.id.btn_reason3);
        EditText other_reason = (EditText) mView.findViewById(R.id.txt_incomplete_reason);

        reason1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reason = 0;
                other_reason.setVisibility(View.GONE);
            }
        });

        reason2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reason = 1;
                other_reason.setVisibility(View.GONE);
            }
        });
        reason3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reason = 2;
                other_reason.setVisibility(View.VISIBLE);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(MainDriverActivity.this);
        builder.setPositiveButton(getResources().getString(R.string.string_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                reason_comment = other_reason.getText().toString();
                if(reason == 2 && reason_comment.equals("")) {
//                    other_reason.setError(getResources().getString(R.string.string_enter_reason));
                    Toast.makeText(MainDriverActivity.this, getResources().getString(R.string.string_enter_reason), Toast.LENGTH_LONG ).show();
                    return;
                } else {
                    mDatabase.getReference("ride/"+ride_uuid).removeValue();
                    onSaveTriptoDB(0);
                    Intent intent=new Intent(MainDriverActivity.this, OffersActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }).setNegativeButton(getResources().getString(R.string.string_close), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setCancelable(false);

        final AlertDialog  deleteDialog = builder.create();
        deleteDialog.setView(mView);

        deleteDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

            }
        });
        deleteDialog.show();
    }

    private void onCancelTrip() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.ThemeOverlay_App_BottomSheetDialog);
        bottomSheetDialog.setContentView(R.layout.passenger_cancel_trip);
        bottomSheetDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        bottomSheetDialog.setCancelable(false);

        Button btn_agree = (Button) bottomSheetDialog.findViewById(R.id.btn_dont_want);
        Button cancel = (Button) bottomSheetDialog.findViewById(R.id.btn_cancel_dialog);

        btn_agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.cancel();
                showQuestionDialog();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.cancel();
            }
        });
//        bottomSheetDialog.getWindow().setDimAmount(0);
        bottomSheetDialog.show();
    }

    private void showQuestionDialog() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.ThemeOverlay_App_BottomSheetDialog);
        bottomSheetDialog.setContentView(R.layout.driver_cancel_question);
        bottomSheetDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        bottomSheetDialog.setCancelable(false);

        Button reason1 = (Button) bottomSheetDialog.findViewById(R.id.btn_didnot_showup);
        Button reason2 = (Button) bottomSheetDialog.findViewById(R.id.btn_fake_offer);
        Button reason3 = (Button) bottomSheetDialog.findViewById(R.id.btn_refuse_trip);
        Button reason4 = (Button) bottomSheetDialog.findViewById(R.id.btn_other_reason);
        Button reason5 = (Button) bottomSheetDialog.findViewById(R.id.btn_close_question);

        reason1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.cancel();
                sendNotification(getResources().getString(R.string.string_notify_driver_cancel));
                mDatabase.getReference("ride/"+ride_uuid).removeValue();
                onSaveTriptoDB(0);
                Intent intent=new Intent(MainDriverActivity.this, OffersActivity.class);
                startActivity(intent);
                finish();
            }
        });

        reason2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.cancel();
                sendNotification(getResources().getString(R.string.string_notify_driver_cancel));
                mDatabase.getReference("ride/"+ride_uuid).removeValue();
                onSaveTriptoDB(0);
                Intent intent=new Intent(MainDriverActivity.this, OffersActivity.class);
                startActivity(intent);
                finish();
            }
        });

        reason3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.cancel();
                sendNotification(getResources().getString(R.string.string_notify_driver_cancel));
                mDatabase.getReference("ride/"+ride_uuid).removeValue();
                onSaveTriptoDB(0);
                Intent intent=new Intent(MainDriverActivity.this, OffersActivity.class);
                startActivity(intent);
                finish();
            }
        });

        reason4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.cancel();
                showTextDialog();
            }
        });

        reason5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.cancel();
            }
        });

//        bottomSheetDialog.getWindow().setDimAmount(0);
        bottomSheetDialog.show();
    }

    private void showTextDialog() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.ThemeOverlay_App_BottomSheetDialog);
        bottomSheetDialog.setContentView(R.layout.passenger_cancel_text);
        bottomSheetDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        bottomSheetDialog.setCancelable(false);

        Button confrim = (Button) bottomSheetDialog.findViewById(R.id.btn_reason_confirm);
        Button close = (Button) bottomSheetDialog.findViewById(R.id.btn_close_text);

        confrim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.cancel();
                sendNotification(getResources().getString(R.string.string_notify_driver_cancel));
                mDatabase.getReference("ride/"+ride_uuid).removeValue();
                onSaveTriptoDB(0);
                Intent intent=new Intent(MainDriverActivity.this, OffersActivity.class);
                startActivity(intent);
                finish();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.cancel();
            }
        });

//        bottomSheetDialog.getWindow().setDimAmount(0);
        bottomSheetDialog.show();
    }

    private String getLocationAddress(LatLng location){
        try {
            addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1);
            String city = addresses.get(0).getLocality();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();
            String throughFare = addresses.get(0).getThoroughfare();

            return knownName + " " + throughFare + ", " + postalCode + " " + city;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "No";
    }

    public void setDistance1(LatLng start_location, LatLng end_location) {
        LatLng origin = start_location;
        LatLng dest = end_location;
        String url = getDirectionsUrl(origin, dest);
        url = url + "&key=AIzaSyBBymj4yxxTiMRQ6qAHndQBVbBpoUCdp94";
        // Request a string response from the provided URL.
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            JSONArray routes = jsonObject.getJSONArray("routes");
                            JSONObject object = routes.getJSONObject(0);
                            JSONArray legs = object.getJSONArray("legs");
                            JSONObject legsObjects = legs.getJSONObject(0);

//get the distance
                            JSONObject distance = legsObjects.getJSONObject("distance");
                            String dis = distance.getString("text");
                            setMarkerEnd(dis);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queue.add(stringRequest);
    }
    public void setDistance2(LatLng start_location, LatLng end_location) {
        LatLng origin = start_location;
        LatLng dest = end_location;
        String url = getDirectionsUrl(origin, dest);
        url = url + "&key=AIzaSyBBymj4yxxTiMRQ6qAHndQBVbBpoUCdp94";
        // Request a string response from the provided URL.
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            JSONArray routes = jsonObject.getJSONArray("routes");
                            JSONObject object = routes.getJSONObject(0);
                            JSONArray legs = object.getJSONArray("legs");
                            JSONObject legsObjects = legs.getJSONObject(0);

//get the distance
                            JSONObject distance = legsObjects.getJSONObject("distance");
                            String dis = distance.getString("text");
                            setMarkerStart(dis);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queue.add(stringRequest);
    }

    public void setMarkerStart(String distance) {
        if(start_marker == null && mMap != null) {
            start_marker = mMap.addMarker((start_markerOptions).title(distance));
            start_marker.showInfoWindow();
        }
    }

    public void setMarkerEnd(String distance) {
        if(end_marker == null && mMap != null) {
            end_marker = mMap.addMarker((end_markerOptions).title(distance));
            end_marker.showInfoWindow();
        }
    }

    private void onSetMarker() {
        start_markerOptions.position(startLocation);
        mMap.addMarker(start_markerOptions);
        for(int i = 1; i < locations.size(); i++) {
            end_markerOptions.position(locations.get(i));
            mMap.addMarker(end_markerOptions);
        }
    }

    private void drawRoute() {
        if(mMap != null) {
            mMap.clear();
        }
        onSetMarker();
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
                    } else if(point.get("distance") != null) {
                        point = path.get(j);
                    }
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.BLACK);
                lineOptions.geodesic(true);

            }

            if(mMap != null) {
                mMap.addPolyline(lineOptions);
            }
        }
    }

    public void onPriceClick(View v) {
        switch (v.getId()) {
            case R.id.btn_price1:
                setOfferPrice(Integer.parseInt(strPrice[0]));
                break;
            case R.id.btn_price2:
                setOfferPrice(Integer.parseInt(strPrice[1]));
                break;
            case R.id.btn_price3:
                setOfferPrice(Integer.parseInt(strPrice[2]));
                break;
            case R.id.btn_price4:
                setOfferPrice(Integer.parseInt(strPrice[3]));
                break;
            case R.id.btn_price5:
                setOfferPrice(Integer.parseInt(strPrice[4]));
                break;
            case R.id.btn_price6:
                setOfferPrice(Integer.parseInt(strPrice[5]));
                break;
            case R.id.btn_price7:
                setOfferPrice(Integer.parseInt(strPrice[6]));
                break;
            case R.id.btn_price8:
                setOfferPrice(Integer.parseInt(strPrice[7]));
                break;
            default:
                setOfferPrice(Integer.parseInt(strPrice[8]));
                break;
        }
    }

    private void onShowMoreRate() {
        LayoutInflater factory = LayoutInflater.from(MainDriverActivity.this);
        final View mView = factory.inflate(R.layout.other_rate, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainDriverActivity.this);
        builder.setPositiveButton(getResources().getString(R.string.string_cancel), null);
        final AlertDialog  deleteDialog = builder.create();
        deleteDialog.setView(mView);

        setOtherPrice(mView, deleteDialog);

        deleteDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

            }
        });
        deleteDialog.show();
    }

    private void setOtherPrice(View mView, AlertDialog deleteDialog) {
        Button btn;
        for(int i = 8; i < strPrice.length; i++) {
            switch (i) {
                case 8:
                    btn = mView.findViewById(R.id.btn_price9);
                    break;
                case 9:
                    btn = mView.findViewById(R.id.btn_price10);
                    break;
                case 10:
                    btn = mView.findViewById(R.id.btn_price11);
                    break;
                case 11:
                    btn = mView.findViewById(R.id.btn_price12);
                    break;
                case 12:
                    btn = mView.findViewById(R.id.btn_price13);
                    break;
                case 13:
                    btn = mView.findViewById(R.id.btn_price14);
                    break;
                case 14:
                    btn = mView.findViewById(R.id.btn_price15);
                    break;
                case 15:
                    btn = mView.findViewById(R.id.btn_price16);
                    break;
                case 16:
                    btn = mView.findViewById(R.id.btn_price17);
                    break;
                case 17:
                    btn = mView.findViewById(R.id.btn_price18);
                    break;
                case 18:
                    btn = mView.findViewById(R.id.btn_price19);
                    break;
                case 19:
                    btn = mView.findViewById(R.id.btn_price20);
                    break;
                case 20:
                    btn = mView.findViewById(R.id.btn_price21);
                    break;
                case 21:
                    btn = mView.findViewById(R.id.btn_price22);
                    break;
                case 22:
                    btn = mView.findViewById(R.id.btn_price23);
                    break;
                case 23:
                    btn = mView.findViewById(R.id.btn_price24);
                    break;
                case 24:
                    btn = mView.findViewById(R.id.btn_price25);
                    break;
                case 25:
                    btn = mView.findViewById(R.id.btn_price26);
                    break;
                case 26:
                    btn = mView.findViewById(R.id.btn_price27);
                    break;
                case 27:
                    btn = mView.findViewById(R.id.btn_price28);
                    break;
                case 28:
                    btn = mView.findViewById(R.id.btn_price29);
                    break;
                case 29:
                    btn = mView.findViewById(R.id.btn_price30);
                    break;
                case 30:
                    btn = mView.findViewById(R.id.btn_price31);
                    break;
                case 31:
                    btn = mView.findViewById(R.id.btn_price32);
                    break;
                case 32:
                    btn = mView.findViewById(R.id.btn_price33);
                    break;
                case 33:
                    btn = mView.findViewById(R.id.btn_price34);
                    break;
                case 34:
                    btn = mView.findViewById(R.id.btn_price35);
                    break;
                case 35:
                    btn = mView.findViewById(R.id.btn_price36);
                    break;
                case 36:
                    btn = mView.findViewById(R.id.btn_price37);
                    break;
                case 37:
                    btn = mView.findViewById(R.id.btn_price38);
                    break;
                case 38:
                    btn = mView.findViewById(R.id.btn_price39);
                    break;
                case 39:
                    btn = mView.findViewById(R.id.btn_price40);
                    break;
                case 40:
                    btn = mView.findViewById(R.id.btn_price41);
                    break;
                case 41:
                    btn = mView.findViewById(R.id.btn_price42);
                    break;
                case 42:
                    btn = mView.findViewById(R.id.btn_price43);
                    break;
                case 43:
                    btn = mView.findViewById(R.id.btn_price44);
                    break;
                default:
                    btn = mView.findViewById(R.id.btn_price9);
                    break;
            }
            btn.setText(strPrice[i]);
            final String price = strPrice[i];
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setOfferPrice(Integer.parseInt(price));
                    deleteDialog.dismiss();
                }
            });
        }
    }

    private void onGetUserInfo() {
        mRef = mDatabase.getReference("user/" + mAuth.getUid() + "/avatar");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String driver_avatar_link = dataSnapshot.getValue(String.class);
                if (driver_avatar_link != null && driver_avatar_link != "") {
                    Common.getInstance().setAvatar(driver_avatar_link);
                    onLocaAvatar(driver_avatar_link);
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
                Intent intent=new Intent(getApplicationContext(), OffersActivity.class);
                startActivity(intent);
                finish();
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
            Toast.makeText(MainDriverActivity.this, getResources().getString(R.string.string_offline), Toast.LENGTH_LONG ).show();
        }else{
            mDatabase.getReference("user/"+mAuth.getUid()+"/type").setValue("user");
            mDatabase.getReference("user/"+mAuth.getUid()+"/join").setValue("off");
            Intent intent=new Intent(getBaseContext(), HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void onSaveTriptoDB(int status) {
        String start = Common.getInstance().getStart_address();
        String end = Common.getInstance().getEnd_address();
        String str_price = String.valueOf(price) + "MXN";
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:s");
        String str_date = inputFormat.format(date);

        JsonObject json = new JsonObject();
        json.addProperty("uid", Common.getInstance().getUser_uid());
        json.addProperty("driver_uid", mAuth.getUid());
        json.addProperty("start", start);
        json.addProperty("end", end);
        json.addProperty("price", str_price);
        json.addProperty("status", status);
        json.addProperty("date", str_date);
        json.addProperty("type", 0);

        try {
            Ion.with(this)
                    .load(Common.getInstance().getBaseURL()+"api/addtrip")
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (result != null) {
                            } else {
                            }
                        }
                    });
        }catch(Exception e){
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void onCheckUnreadChat() {
        String user_id;
        String agency_id;
        user_id = Common.getInstance().getUser_uid();
        agency_id = mAuth.getUid();

        oppMsgRef = mDatabase.getReference("chat/agency/"+ agency_id + "/" + user_id);
        _childListener = oppMsgRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ChatMessage value = dataSnapshot.getValue(ChatMessage.class);
                if(value!=null && value.getStatus().equals("no")){
                    unread ++;
                }
                onSetUnread();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void onSetUnread() {
        if(unread != 0) {
            txt_counter_badge.setText(String.valueOf(unread));
            txt_counter_badge.setVisibility(View.VISIBLE);
        } else {
            txt_counter_badge.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == 0) {
                unread = 0;
                onSetUnread();
            }
        }
    }

    private void onRemoveChat() {
        String user_id;
        String agency_id;
        user_id = Common.getInstance().getUser_uid();
        agency_id = Common.getInstance().getDriver_uid();
        mDatabase.getReference("chat/user/" + user_id + "/" + agency_id).removeValue();
        mDatabase.getReference("chat/agency/"+ agency_id + "/" + user_id).removeValue();
    }
}