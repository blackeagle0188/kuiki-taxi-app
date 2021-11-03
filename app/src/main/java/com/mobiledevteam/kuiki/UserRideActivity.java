package com.mobiledevteam.kuiki;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.common.reflect.TypeToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.mobiledevteam.kuiki.Adapter.DriverOffer;
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import co.intentservice.chatui.models.ChatMessage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserRideActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private DatabaseReference mRideRef;
    private DatabaseReference oppMsgRef;
    private TextView _driverName;
    private TextView _startAddress;
    private TextView _endAddress;
    private TextView _brand;
    private TextView _color;
    private TextView _carNumber;
    private Button _callPhone;
    private Button _whatsapp;
    private Button _btnBook;
    private TextView _btnCancel;
    private LinearLayout btn_passenger_call;
    private TextView passenger_notify;
    private ImageView img_driver_avatar;
    private TextView driver_car_info;
    private LinearLayout layout_before_arrive;
    private LinearLayout layout_after_arrive;
    private TextView txt_timer;
    private TextView txt_driver_review;
    private SupportMapFragment mapFragment;
    private ChildEventListener _childListener;
    private int unread = 0;
    private TextView txt_counter_badge;

    private int height = 100;
    private int width = 100;
    private Bitmap start_smallMarker;
    private Bitmap end_smallMarker;
    private Bitmap car_smallMarker;
    private String phoneNumber;
    private String bookingStatus="none";
    private String uniqueId;
    private String pay_type;
    private Boolean checkdriver = true;
    private String startAddress;
    private String endAddress;
    private ArrayList<LatLng> locations;
    private double driverLat = 0;
    private double driverLong = 0;
    private LatLng driverLocation;
    private String brand;
    private String plate;
    private String color_name;

    private DriverOffer accepted_offer;
    private CountDownTimer countDownTimer;

    private OkHttpClient httpClient = new OkHttpClient();
    private String paymentIntentClientSecret;
    private String paytype;
    private String key = "";

    MarkerOptions start_markerOptions;
    MarkerOptions end_markerOptions;
    MarkerOptions car_markerOptions;

    PolylineOptions lineOptions = null;

    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
    private static final String CONFIG_CLIENT_ID = "AdDyljii_kmwdZlRZjLDXBQLINWVD1yVhLirGdhgobbNoRhUVWz5YXRBV0rumFi6kj50grNRCGMGzTKW";
    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;

    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID)
            .acceptCreditCards(true)
            .rememberUser(false);

    PayPalPayment thingToBuy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_ride);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);

        Common.getInstance().setUser_uid(mAuth.getUid());
        pay_type = Common.getInstance().getPay_type();
        startAddress =Common.getInstance().getStart_address();
        endAddress = Common.getInstance().getEnd_address();
        key = getIntent().getStringExtra("uuid");
        if(key.equals("null")) {
            uniqueId = Common.getInstance().getRide_uuid();
        } else {
            uniqueId = key;
        }
        locations = Common.getInstance().getLocations();

        paytype = Common.getInstance().getPay_type();

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

        BitmapDrawable bitmapdraw3 = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_car_front);
        Bitmap b3 = bitmapdraw3.getBitmap();
        car_markerOptions = new MarkerOptions();
        car_smallMarker = Bitmap.createScaledBitmap(b3, 100, 100, false);
        car_markerOptions.icon(BitmapDescriptorFactory.fromBitmap(car_smallMarker));

        startView();

        mapFragment.getMapAsync(this);

//        drawRoute();
    }

    private void startView(){
        _driverName = (TextView)findViewById(R.id.txt_drivername);
        _brand = (TextView) findViewById(R.id.car_brand);
        _color = (TextView) findViewById(R.id.car_color);
        _carNumber = (TextView)findViewById(R.id.txt_carnumber);
        _startAddress = (TextView)findViewById(R.id.ride_start_address);
        _endAddress = (TextView)findViewById(R.id.end_address);
        _callPhone = (Button)findViewById(R.id.img_phone);
        _whatsapp = (Button) findViewById(R.id.btn_driver_whatsapp);
        _btnBook = (Button)findViewById(R.id.btn_booking);
        _btnCancel = (TextView)findViewById(R.id.btn_passenger_cancel);
        btn_passenger_call = (LinearLayout) findViewById(R.id.btn_passenger_call);
        passenger_notify = (TextView) findViewById(R.id.passenger_notify);
        img_driver_avatar = (ImageView) findViewById(R.id.img_driver_avatar);
        driver_car_info = (TextView) findViewById(R.id.driver_car_info);
        layout_before_arrive = (LinearLayout) findViewById(R.id.layout_before_arrive);
        layout_after_arrive = (LinearLayout) findViewById(R.id.layout_after_arrive);
        txt_timer = (TextView) findViewById(R.id.txt_timer);
        txt_driver_review = (TextView) findViewById(R.id.txt_driver_review);
        txt_counter_badge = (TextView) findViewById(R.id.txt_counter_badge);

        if(key.equals("null")) {
            onSetSomeInfo();
        } else {
            onGetExistTripInfo();
        }

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        BitmapDrawable bitmapdraw1 = (BitmapDrawable)getResources().getDrawable(R.drawable.ic_map_start);
        Bitmap b1 = bitmapdraw1.getBitmap();
        start_smallMarker = Bitmap.createScaledBitmap(b1, 70, height, false);
        BitmapDrawable bitmapdraw2 = (BitmapDrawable)getResources().getDrawable(R.drawable.ic_map_end);
        Bitmap b2 = bitmapdraw2.getBitmap();
        end_smallMarker = Bitmap.createScaledBitmap(b2, 70, height, false);
        BitmapDrawable bitmapdraw3 = (BitmapDrawable)getResources().getDrawable(R.drawable.ic_car_front);
        Bitmap b3 = bitmapdraw3.getBitmap();
        car_smallMarker = Bitmap.createScaledBitmap(b3, width, height, false);

        _startAddress.setText(startAddress);
        _endAddress.setText(endAddress);

        _callPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(UserRideActivity.this,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(UserRideActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 1);

                }else{
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
                    startActivity(intent);
                }
            }
        });
        _whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra("user_id", mAuth.getUid());
                startActivity(intent);
//                String url = "https://api.whatsapp.com/send?phone=$" + phoneNumber;
//                try {
//                    PackageManager packageManager = getApplicationContext().getPackageManager();
//                    packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
//                    Intent i = new Intent(Intent.ACTION_VIEW);
//                    i.setData(Uri.parse(url));
//                    startActivity(i);
//                } catch (PackageManager.NameNotFoundException e) {
//                    Toast.makeText(UserRideActivity.this, getResources().getString(R.string.string_no_whatsapp), Toast.LENGTH_LONG ).show();
//                }
            }
        });
        btn_passenger_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(UserRideActivity.this,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(UserRideActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 1);

                }else{
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
                    startActivity(intent);
                }
            }
        });
        _btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookingRide();
            }
        });
        _btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bookingStatus.equals("waiting")) {
                    sendNotification(getResources().getString(R.string.string_notify_passenger_cancel));
                    mDatabase.getReference("ride/"+uniqueId).removeValue();
                    Intent intent = new Intent(UserRideActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    showCancelDialog();
                }
            }
        });
    }

    private void onSetSomeInfo() {
        accepted_offer = Common.getInstance().getAcceptedOffer();
        Picasso.get().load(accepted_offer.getmImage()).into(img_driver_avatar);
        _driverName.setText(Common.getInstance().getAcceptedOffer().getName());
        String accepted_price = Common.getInstance().getAcceptedOffer().getPrice();
        String accepted_time = Common.getInstance().getAcceptedOffer().getTime();
        String notify_text = String.format(getResources().getString(R.string.string_passenger_accept), accepted_price, accepted_time);
        passenger_notify.setText(notify_text);
        txt_driver_review.setText(accepted_offer.getReview());
        onCheckUnreadChat();

        mRef = mDatabase.getReference("user/"+accepted_offer.getUid());
        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.getKey().equals("name")){
                    _driverName.setText(dataSnapshot.getValue().toString());
                }
                if (dataSnapshot.getKey().equals("latitude")){
                    driverLat = dataSnapshot.getValue(double.class);
                }
                if (dataSnapshot.getKey().equals("longitude")){
                    driverLong = dataSnapshot.getValue(double.class);
                    if(driverLat != 0 && driverLong != 0) {
                        driverLocation = new LatLng(driverLat, driverLong);
                        mapFragment.getMapAsync(UserRideActivity.this);
                    }
                }
                if (dataSnapshot.getKey().equals("phonenumber")){
                    phoneNumber = dataSnapshot.getValue().toString();
                }
                if (dataSnapshot.getKey().equals("plate")){
                    plate = dataSnapshot.getValue().toString();
                    _carNumber.setVisibility(View.VISIBLE);
                    _carNumber.setText(plate);
                }
                if (dataSnapshot.getKey().equals("brand")){
                    brand = dataSnapshot.getValue().toString();
                    _brand.setText(brand);
                }
                if (dataSnapshot.getKey().equals("color")){
                    String color = getResources().getString(R.string.string_car_color);
                   int color_number = Integer.parseInt(dataSnapshot.getValue().toString());
                   switch (color_number) {
                       case 0:
                           color_name = getResources().getString(R.string.string_color_black);
                           break;
                       case 1:
                           color_name = getResources().getString(R.string.string_color_red);
                           break;
                       case 2:
                           color_name = getResources().getString(R.string.string_color_blue);
                           break;
                       case 3:
                           color_name = getResources().getString(R.string.string_color_gray);
                           break;
                       case 4:
                           color_name = getResources().getString(R.string.string_color_light_gray);
                           break;
                       case 5:
                           color_name = getResources().getString(R.string.string_color_dark_gray);
                           break;
                       case 6:
                           color_name = getResources().getString(R.string.string_color_beige);
                           break;
                       case 7:
                           color_name = getResources().getString(R.string.string_color_brown);
                           break;
                       case 8:
                           color_name = getResources().getString(R.string.string_color_green);
                           break;
                       case 9:
                           color_name = getResources().getString(R.string.string_color_purple);
                           break;
                       case 10:
                           color_name = getResources().getString(R.string.string_color_violet);
                           break;
                       case 11:
                           color_name = getResources().getString(R.string.string_color_white);
                           break;
                       case 12:
                           color_name = getResources().getString(R.string.string_color_yellow);
                           break;
                       case 13:
                           color_name = getResources().getString(R.string.string_color_orange);
                           break;
                   }
                    color += " " + color_name;
                    _color.setText(color);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.getKey().equals("latitude")){
                    driverLat = dataSnapshot.getValue(double.class);
                    driverLocation = new LatLng(driverLat, driverLong);
                    mapFragment.getMapAsync(UserRideActivity.this);
                }
                if (dataSnapshot.getKey().equals("longitude")){
                    driverLong = dataSnapshot.getValue(double.class);
                    driverLocation = new LatLng(driverLat, driverLong);
                    mapFragment.getMapAsync(UserRideActivity.this);
                }
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

        mRideRef = mDatabase.getReference("ride/"+accepted_offer.getRideid());
        mRideRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bookingStatus = dataSnapshot.child("status").getValue(String.class);
                String type = dataSnapshot.child("paytype").getValue(String.class);
                String passenger_offer;
                if (bookingStatus == null){
                    Intent intent = new Intent(UserRideActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    try {
                        passenger_offer = dataSnapshot.child("passenger_offer").getValue(String.class);
                    } catch (Exception e) {
                        passenger_offer = "no";
                    }
                    if (bookingStatus.equals("waiting")){
                        _btnBook.setText(getResources().getString(R.string.string_waiting));
                        _btnBook.setEnabled(false);
                    }else if (bookingStatus.equals("accept")){

                        if(paytype.equals("cash")) {
                            _btnBook.setText(getResources().getString(R.string.string_driver_way));
                            _btnBook.setEnabled(false);
                        } else {
                            _btnBook.setText(getResources().getString(R.string.string_paid));
                            _btnBook.setEnabled(true);
                        }
                        if(passenger_offer.equals("yes")) {
//                            onShowAlert(getResources().getString(R.string.driver_accept));
                        }
                    }else if (bookingStatus.equals("pickup")){
                        layout_before_arrive.setVisibility(View.GONE);
                        passenger_notify.setVisibility(View.GONE);
                        driver_car_info.setText(brand + ", " + color_name + " : " + plate);
                        layout_after_arrive.setVisibility(View.VISIBLE);
                        onCountdownTimer();
                        _btnBook.setText(getResources().getString(R.string.string_passenger_understood));
                        _btnBook.setEnabled(true);
                        mapFragment.getMapAsync(UserRideActivity.this);
                    }else if (bookingStatus.equals("pay")){
                        countDownTimer.cancel();
                        layout_after_arrive.setVisibility(View.GONE);
                        layout_before_arrive.setVisibility(View.VISIBLE);
                        if(type.equals("card")) {
                            Intent intent = new Intent(UserRideActivity.this, CheckoutPaymentActivity.class);
                            startActivity(intent);
                        } else if(type.equals("paypal")) {
                            thingToBuy = new PayPalPayment(new BigDecimal(Common.getInstance().getPay_amount()), "MXN", "Contact information fee",PayPalPayment.PAYMENT_INTENT_SALE);
                            thingToBuy.payeeEmail("sb-orfod6157661@personal.example.com");
                            Intent intent = new Intent(UserRideActivity.this, com.paypal.android.sdk.payments.PaymentActivity.class);
                            // send the same configuration for restart resiliency
                            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

                            intent.putExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_PAYMENT, thingToBuy);

                            startActivityForResult(intent, REQUEST_CODE_PAYMENT);
                        } else if(type.equals("cash")) {
                            _btnBook.setText(getResources().getString(R.string.string_paid));
                            _btnBook.setEnabled(false);
                            mapFragment.getMapAsync(UserRideActivity.this);
                        }
                    }else if (bookingStatus.equals("paid")){
                        if(type.equals("cash")) {
                            _btnBook.setEnabled(true);
                            _btnBook.setText(getResources().getString(R.string.string_complete));
                        } else{
                            _btnBook.setText(getResources().getString(R.string.string_waiting_taxi));
                            _btnBook.setEnabled(false);
                        }

                    } else if (bookingStatus.equals("process")) {
                        _btnBook.setEnabled(true);
                        _btnBook.setText(getResources().getString(R.string.string_complete));
                        mapFragment.getMapAsync(UserRideActivity.this);
                    } else if (bookingStatus.equals("done")) {
                        _btnBook.setEnabled(true);
                        _btnBook.setText(getResources().getString(R.string.string_give_review));
                        mapFragment.getMapAsync(UserRideActivity.this);
                    } else if (bookingStatus.equals("complete")){
                        _btnBook.setEnabled(true);
                        _btnBook.setText(getResources().getString(R.string.string_give_review));
//                        onSaveTriptoDB(1);
                        mapFragment.getMapAsync(UserRideActivity.this);
                        Intent intent = new Intent(UserRideActivity.this, PassengerReview.class);
                        intent.putExtra("ride_id", uniqueId);
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

    private void onGetExistTripInfo() {
        mRef = FirebaseDatabase.getInstance().getReference().child("offer/");
        mRef.orderByChild("ride").equalTo(uniqueId).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for(DataSnapshot ds : snapshot.getChildren()) {
                        String key = ds.getKey();
                        onGetOffer(key);
                    }
                } else {
                    Intent intent = new Intent(UserRideActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void onGetOffer(String key) {
        mRef = mDatabase.getReference("offer/" + key);
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds) {
                if (ds.exists()) {
                    String key = ds.getKey();
                    String ride_id = ds.child("ride").getValue(String.class);
                    String driver_id = ds.child("driver").getValue(String.class);
                    String driver_name = ds.child("driver_name").getValue(String.class);
                    String driver_avatar = ds.child("driver_avatar").getValue(String.class);
                    String driver_brand = ds.child("driver_brand").getValue(String.class);
                    String driver_price = ds.child("price").getValue(String.class);
                    String driver_time = ds.child("time").getValue(String.class);
                    String driver_phone = ds.child("driver_phone").getValue(String.class);
                    String distance = ds.child("distance").getValue(String.class);
                    String status = ds.child("status").getValue(String.class);
                    String driverphone = ds.child("driverphone").getValue(String.class);
                    String review = ds.child("review").getValue(String.class);
                    if(ride_id != null && driver_id != null && driver_name != null && driver_avatar != null && driver_brand != null && driver_price != null && driver_time!= null && distance != null && driverphone != null && status != null && driver_phone != null && review != null) {
                        String mId = ds.getKey();

                        DriverOffer offer = new DriverOffer();
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
                        offer.setPhone(driverphone);
                        offer.setReview(review);

                        Common.getInstance().setAcceptedOffer(offer);
                        onSetSomeInfo();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showCancelDialog() {
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
        bottomSheetDialog.setContentView(R.layout.passenger_cancel_question);
        bottomSheetDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        bottomSheetDialog.setCancelable(false);

        Button reason1 = (Button) bottomSheetDialog.findViewById(R.id.btn_dont_want);
        Button reason2 = (Button) bottomSheetDialog.findViewById(R.id.btn_complain_driver);
        Button reason3 = (Button) bottomSheetDialog.findViewById(R.id.btn_other_reason);
        Button reason4 = (Button) bottomSheetDialog.findViewById(R.id.btn_close_question);

        reason1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.cancel();
                sendNotification(getResources().getString(R.string.string_notify_passenger_cancel));
                mDatabase.getReference("ride/"+uniqueId).removeValue();
                onSaveTriptoDB(0);
                Intent intent = new Intent(UserRideActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        reason2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.cancel();
                showTextDialog();
            }
        });

        reason3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.cancel();
                showTextDialog();
            }
        });

        reason4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.cancel();
            }
        });

//        bottomSheetDialog.getWindow().setDimAmount(0);
        bottomSheetDialog.show();
    }

    private void onSaveTriptoDB(int status) {
        String start = Common.getInstance().getStart_address();
        String end = Common.getInstance().getEnd_address();
        DriverOffer offer = Common.getInstance().getAcceptedOffer();
        String price = offer.getPrice();
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:s");
        String str_date = inputFormat.format(date);

        JsonObject json = new JsonObject();
        json.addProperty("uid", mAuth.getUid());
        json.addProperty("driver_uid", Common.getInstance().getDriver_uid());
        json.addProperty("start", start);
        json.addProperty("end", end);
        json.addProperty("price", price);
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
                sendNotification(getResources().getString(R.string.string_notify_passenger_cancel));
                mDatabase.getReference("ride/"+uniqueId).removeValue();
                onSaveTriptoDB(0);
                Intent intent = new Intent(UserRideActivity.this, HomeActivity.class);
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

    private void sendNotification(String content){
        Map<String,Object> payMap = new HashMap<>();
        Map<String,Object> itemMap = new HashMap<>();
        String token = Common.getInstance().getPhone_token();
        payMap.put("to",Common.getInstance().getPhone_token());
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

    private void onCountdownTimer() {
        countDownTimer = new CountDownTimer(180000, 1000) {

            public void onTick(long millisUntilFinished) {
                String minute = String.format("%02d", (millisUntilFinished / 1000) / 60);
                String second =  String.format("%02d", (millisUntilFinished / 1000) % 60);
                txt_timer.setText(minute + " : " + second);
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
            }

        }.start();
    }

    private void bookingRide(){
        if(!checkdriver){

        }else{
            if(bookingStatus.equals("none")){
            }else if (bookingStatus.equals("waiting")){
            }else if (bookingStatus.equals("accept")){
                if(!paytype.equals("cash")) {
                    if(pay_type.equals("card")){
                        Intent intent = new Intent(UserRideActivity.this, CheckoutPaymentActivity.class);
                        startActivity(intent);
                    }else{
                        mDatabase.getReference("ride/"+uniqueId+"/status").setValue("pay");
                    }
                }

            }else if (bookingStatus.equals("pickup")){
                if(pay_type.equals("card")){
                    Intent intent = new Intent(UserRideActivity.this, CheckoutPaymentActivity.class);
                    startActivity(intent);
                }else{
                    mDatabase.getReference("ride/"+uniqueId+"/status").setValue("pay");
                }
                sendNotification(getResources().getString(R.string.string_notify_passenger_going));
            }else if (bookingStatus.equals("pay")){
            }else if (bookingStatus.equals("paid")){
                mDatabase.getReference("ride/"+uniqueId+"/status").setValue("complete");
            } else if(bookingStatus.equals("process")) {
                mDatabase.getReference("ride/"+uniqueId+"/status").setValue("done");
            } else if (bookingStatus.equals("done")){
//                onSaveTriptoDB(1);
                Intent intent = new Intent(UserRideActivity.this, PassengerReview.class);
                intent.putExtra("ride_id", uniqueId);
                startActivity(intent);
                finish();
            } else if (bookingStatus.equals("complete")){
//                onSaveTriptoDB(1);
                Intent intent = new Intent(UserRideActivity.this, PassengerReview.class);
                intent.putExtra("ride_id", uniqueId);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(mMap == null) {
            mMap = googleMap;
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locations.get(0), 14));
        }
        googleMap.clear();

        drawRoute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == 0) {
                unread = 0;
                onSetUnread();
            }
        }
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm =
                        data.getParcelableExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        Log.i("test::", confirm.toJSONObject().toString(4));
                        Log.i("TEST:::", confirm.getPayment().toJSONObject().toString(4));
                        /**
                         *  TODO: send 'confirm' (and possibly confirm.getPayment() to your server for verification
                         * or consent completion.
                         * See https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
                         * for more details.
                         *
                         * For sample mobile backend interactions, see
                         * https://github.com/paypal/rest-api-sdk-python/tree/master/samples/mobile_backend
                         */
                        //add to code to send payment info
                        setResult(102);

                    } catch (JSONException e) {
                        //Log.e(TAG, "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                //Log.i(TAG, "The user canceled.");
                Toast.makeText(getBaseContext(), "The user canceled.", Toast.LENGTH_LONG).show();
            } else if (resultCode == com.paypal.android.sdk.payments.PaymentActivity.RESULT_EXTRAS_INVALID) {
                //Log.i(TAG,"An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
                Toast.makeText(getBaseContext(), "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void onPaymentSuccess(@NonNull final Response response) throws IOException {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> responseMap = gson.fromJson(
                Objects.requireNonNull(response.body()).string(),
                type
        );

        paymentIntentClientSecret = responseMap.get("clientSecret");
    }

    private final class PayCallback implements Callback {
        @NonNull private final WeakReference<UserRideActivity> activityRef;

        PayCallback(@NonNull UserRideActivity activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            final UserRideActivity activity = activityRef.get();
            Log.d("failure::::", e.getMessage());
            if (activity == null) {
                return;
            }
            Log.d("failure::::", e.getMessage());
//            Toast.makeText(CheckoutPaymentActivity.this, "Error: " + e.toString(), Toast.LENGTH_LONG ).show();
        }

        @Override
        public void onResponse(@NonNull Call call, @NonNull final Response response)
                throws IOException {
            final UserRideActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }

            if (!response.isSuccessful()) {
                Toast.makeText(activity, "Error: " + response.toString(), Toast.LENGTH_LONG).show();
            } else {
                activity.onPaymentSuccess(response);
            }
        }
    }

    public void onFuturePaymentPressed(View pressed) {
        Intent intent = new Intent(UserRideActivity.this,
                PayPalFuturePaymentActivity.class);

        startActivityForResult(intent, REQUEST_CODE_FUTURE_PAYMENT);
    }

    @Override
    protected void onStop() {
        Log.d("test","stoped");
        stopService(new Intent(this, PayPalService.class));
        super.onStop();
    }

    public void onFuturePaymentPurchasePressed(View pressed) {
        // Get the Application Correlation ID from the SDK
        String correlationId = PayPalConfiguration
                .getApplicationCorrelationId(this);

        Log.i("FuturePaymentExample", "Application Correlation ID: "
                + correlationId);

        // TODO: Send correlationId and transaction details to your server for
        // processing with
        // PayPal...
        Toast.makeText(getApplicationContext(),
                "App Correlation ID received from SDK", Toast.LENGTH_LONG)
                .show();
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    private void sendAuthorizationToServer(PayPalAuthorization authorization) {

    }

    private void onShowAlert(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        // Create the AlertDialog object and return it
        builder.create();
        builder.show();
    }

    private void onSetMarker() {
        if(locations.size() == 1) {
            start_markerOptions.position(locations.get(0));
            mMap.addMarker(start_markerOptions);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locations.get(0),14));
        } else {
            start_markerOptions.position(locations.get(0));
            mMap.addMarker(start_markerOptions);
            for(int i = 1; i < locations.size(); i++) {
                end_markerOptions.position(locations.get(i));
                mMap.addMarker(end_markerOptions);
            }
        }

        if(driverLocation != null) {
            car_markerOptions.position(driverLocation);
            mMap.addMarker(car_markerOptions);
        }
    }

    private void drawRoute() {
        if(mMap != null) {
            mMap.clear();
        }
        onSetMarker();
        if(lineOptions == null) {
            LatLng origin = locations.get(0);
            LatLng dest = locations.get(locations.size() - 1);
            String url = getDirectionsUrl(origin, dest);
            url = url + "&key=AIzaSyBBymj4yxxTiMRQ6qAHndQBVbBpoUCdp94";
            DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute(url);
        } else {
            mMap.addPolyline(lineOptions);
        }

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

            if(lineOptions != null && mMap != null) {
                mMap.addPolyline(lineOptions);
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
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                mMap.animateCamera(cu);
            }
        }
    }

    private void onCheckUnreadChat() {
        String user_id;
        String agency_id;
        user_id = mAuth.getUid();
        agency_id = accepted_offer.getUid();

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

    private void onRemoveChat() {
        String user_id;
        String agency_id;
        user_id = Common.getInstance().getUser_uid();
        agency_id = Common.getInstance().getDriver_uid();
        mDatabase.getReference("chat/user/" + user_id + "/" + agency_id).removeValue();
        mDatabase.getReference("chat/agency/"+ agency_id + "/" + user_id).removeValue();
    }
}