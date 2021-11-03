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
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.mobiledevteam.kuiki.Adapter.DeliveryRequest;
import com.mobiledevteam.kuiki.Adapter.DriverOffer;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import co.intentservice.chatui.models.ChatMessage;

import static java.util.Locale.getDefault;

public class MainDeliveryActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private DatabaseReference oppMsgRef;
    private Geocoder geocoder;
    private ChildEventListener _childListener;

    private Button btnBack;
    private Button btnIncreasePrice;
    private Button btnDecreasePrice;
    private Button btnAccept;
    private ImageView userAvatar;
    private TextView userName;
    private TextView pickupAddress;

    private LinearLayout layout_service;
    private TextView txtServiceCompany;
    private TextView txtServiceNumber;
    private TextView txtServicePrice;

    private LinearLayout layout_restaurant;
    private TextView txtRestaurantName;
    private TextView txtOrders;

    private LinearLayout layout_supermarket;
    private TextView txtSupermarketName;
    private TextView txtSupermarketOrders;

    private TextView txtWaitingChange;
    private EditText txtChangePrice;
    private ProgressBar progressBar;
    private LinearLayout layout_create;
    private RelativeLayout layout_communication;
    private Button btnCall;
    private Button btnChat;
    private Button btnWhatsapp;
    private LinearLayout layout_change_price;
    private Button btn_change_price;
    private TextView txt_offering_rate;
    private TextView txt_driver_timer;
    private TextView btn_driver_cancel;
    private TextView txt_waiting_customr;
    private TextView txt_counter_badge;
    private TextView txt_rating_value;

    private LinearLayout layout_accept_btn;

    private DeliveryRequest deliveryRequest;
    private String delivery_id = "";
    private String uniqueId = "";
    private int price = 0;
    private int change_price = 0;
    private int time = 15;
    private String bookingStatus = "";
    private String userPhone;
    private Thread thread;
    private boolean isAccepted = false;
    private boolean changedPrice = false;
    private int reason = 0;
    private String reason_comment = "";
    private int unread = 0;

    private CountDownTimer countDownTimer;
    private boolean isNew = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_delivery);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        geocoder = new Geocoder(this, getDefault());

        Intent intent = getIntent();
        delivery_id = intent.getStringExtra("id");
        Common.getInstance().setDriver_uid(mAuth.getUid());
        String status = intent.getStringExtra("type");

        if(status.equals("new")) {
            isNew = true;
        } else {
            isNew = false;
            uniqueId = intent.getStringExtra("offer_id");
        }

        btnBack = (Button) findViewById(R.id.btn_back);
        btnIncreasePrice = (Button) findViewById(R.id.btn_price_plus);
        btnDecreasePrice = (Button) findViewById(R.id.btn_price_minus);
        btnAccept = (Button) findViewById(R.id.btn_accept);
        userAvatar = (ImageView) findViewById(R.id.img_driver_avatar);
        userName = (TextView) findViewById(R.id.txt_user_name);
        pickupAddress = (TextView) findViewById(R.id.pickup_address);
        layout_service = (LinearLayout) findViewById(R.id.layout_service_payment);
        txtServiceCompany = (TextView) findViewById(R.id.txt_service_company);
        txtServiceNumber = (TextView) findViewById(R.id.txt_service_number);
        txtServicePrice = (TextView) findViewById(R.id.txt_service_price);
        layout_restaurant = (LinearLayout) findViewById(R.id.layout_restaurant);
        txtRestaurantName = (TextView) findViewById(R.id.txt_restaurant_name);
        txtOrders = (TextView) findViewById(R.id.txt_orders);
        layout_supermarket = (LinearLayout) findViewById(R.id.layout_supermarket);
        txtSupermarketName = (TextView) findViewById(R.id.txt_supermarket_name);
        txtSupermarketOrders = (TextView) findViewById(R.id.txt_supermarket_orders);
        txtChangePrice = (EditText) findViewById(R.id.txt_changed_price);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        layout_create = (LinearLayout) findViewById(R.id.layout_create_offer);
        layout_communication = (RelativeLayout) findViewById(R.id.layout_communication);
        btnCall = (Button) findViewById(R.id.img_phone);
        btnChat = (Button) findViewById(R.id.btn_driver_chat);
        btnWhatsapp = (Button) findViewById(R.id.btnWhatsapp);
        layout_change_price = (LinearLayout) findViewById(R.id.layout_change_price);
        btn_change_price = (Button)findViewById(R.id.btn_change_price);
        layout_accept_btn = (LinearLayout) findViewById(R.id.layout_accpted_button);
        txtWaitingChange = (TextView) findViewById(R.id.txtWaitingChange);
        txt_offering_rate = (TextView) findViewById(R.id.txt_offering_rate);
        txt_driver_timer = (TextView) findViewById(R.id.txt_driver_timer);
        btn_driver_cancel = (TextView) findViewById(R.id.btn_driver_cancel);
        txt_waiting_customr = (TextView) findViewById(R.id.txt_waiting_customr);
        txt_counter_badge = (TextView) findViewById(R.id.txt_counter_badge);
        txt_rating_value = (TextView) findViewById(R.id.txt_rating_value);
        txtOrders.setMovementMethod(new ScrollingMovementMethod());
        txtSupermarketOrders.setMovementMethod(new ScrollingMovementMethod());
        txtSupermarketOrders.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                txtSupermarketOrders.getParent().requestDisallowInterceptTouchEvent(true);

                return false;
            }
        });
        txtOrders.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                txtOrders.getParent().requestDisallowInterceptTouchEvent(true);

                return false;
            }
        });


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_driver_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCancelTrip();
            }
        });

        btnIncreasePrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                change_price = change_price + 5;
                txtChangePrice.setText(String.valueOf(change_price) + " MXN");
            }
        });

        btnDecreasePrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(change_price - 5 >= 35) {
                    change_price = change_price - 5;
                    txtChangePrice.setText(String.valueOf(change_price) + " MXN");
                }
            }
        });

        btnWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://api.whatsapp.com/send?phone=$" + userPhone;
                try {
                    PackageManager packageManager = getApplicationContext().getPackageManager();
                    packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                } catch (PackageManager.NameNotFoundException e) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.string_no_whatsapp), Toast.LENGTH_LONG ).show();
                }
            }
        });

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookingRide();
            }
        });

        btn_change_price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChangePrice();
            }
        });

        onSetView();
        onGetDeliveryStatus();
        onRemoveTripPanel();
    }

    private void onSetView() {
        ArrayList<DeliveryRequest> deliveryRequests = Common.getInstance().getDeliveryRequests();
        for(int i = 0; i < deliveryRequests.size(); i++) {
            DeliveryRequest offer = deliveryRequests.get(i);
            if(offer.getId().equals(delivery_id)) {
                deliveryRequest = offer;
                break;
            }
        }

        Picasso.get().load(Common.getInstance().getBaseURL()+"/backend/" + deliveryRequest.getUserAvatar()).into(userAvatar);
        userName.setText(deliveryRequest.getUserName());

        if(deliveryRequest.getType() == 0) {
            layout_service.setVisibility(View.VISIBLE);
            pickupAddress.setText(deliveryRequest.getServicePayment().getPickUp());
            txtServiceCompany.setText(deliveryRequest.getServicePayment().getServiceCompany());
            txtServiceNumber.setText(deliveryRequest.getServicePayment().getServiceNumber());
            txtServicePrice.setText(String.valueOf(deliveryRequest.getServicePayment().getPrice()) + "MXN");
            price = deliveryRequest.getServicePayment().getPrice();
        } else if(deliveryRequest.getType() == 1) {
            layout_restaurant.setVisibility(View.VISIBLE);
            pickupAddress.setText(deliveryRequest.getRestaurant().getPickUp());
            txtRestaurantName.setText(deliveryRequest.getRestaurant().getRestaurantname());
            txtOrders.setText(deliveryRequest.getRestaurant().getOrder());
            price = 0;
        } else if(deliveryRequest.getType() == 2) {
            layout_supermarket.setVisibility(View.VISIBLE);
            pickupAddress.setText(deliveryRequest.getSupermarket().getPickUp());
            txtSupermarketName.setText(deliveryRequest.getSupermarket().getSupermarketname());
            txtSupermarketOrders.setText(deliveryRequest.getSupermarket().getOrder());
            price = 0;
        }

        if(price >= 35) {
            change_price = price;
        } else {
            change_price = 35;
        }
        AddConstantTextInEditText(txtChangePrice, " MXN");
        txtChangePrice.setText(String.valueOf(change_price) + " MXN");
        txt_rating_value.setText(deliveryRequest.getReview());
        checkOfferStatus();
        onGetUserInfo();
    }

    private void onGetUseInfo() {

    }

    public void onPickTime(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnTime1:
                v.setBackground(getResources().getDrawable(R.drawable.back_green_button));
                findViewById(R.id.btnTime2).setBackground(getResources().getDrawable(R.drawable.back_black_button));
                findViewById(R.id.btnTime3).setBackground(getResources().getDrawable(R.drawable.back_black_button));
                findViewById(R.id.btnTime4).setBackground(getResources().getDrawable(R.drawable.back_black_button));
                time = 15;
                break;
            case R.id.btnTime2:
                v.setBackground(getResources().getDrawable(R.drawable.back_green_button));
                findViewById(R.id.btnTime1).setBackground(getResources().getDrawable(R.drawable.back_black_button));
                findViewById(R.id.btnTime3).setBackground(getResources().getDrawable(R.drawable.back_black_button));
                findViewById(R.id.btnTime4).setBackground(getResources().getDrawable(R.drawable.back_black_button));
                time = 30;
                break;
            case R.id.btnTime3:
                v.setBackground(getResources().getDrawable(R.drawable.back_green_button));
                findViewById(R.id.btnTime1).setBackground(getResources().getDrawable(R.drawable.back_black_button));
                findViewById(R.id.btnTime2).setBackground(getResources().getDrawable(R.drawable.back_black_button));
                findViewById(R.id.btnTime4).setBackground(getResources().getDrawable(R.drawable.back_black_button));
                time = 45;
                break;
            case R.id.btnTime4:
                v.setBackground(getResources().getDrawable(R.drawable.back_green_button));
                findViewById(R.id.btnTime1).setBackground(getResources().getDrawable(R.drawable.ripple_effect_black));
                findViewById(R.id.btnTime2).setBackground(getResources().getDrawable(R.drawable.ripple_effect_black));
                findViewById(R.id.btnTime3).setBackground(getResources().getDrawable(R.drawable.ripple_effect_black));
                time = 60;
                break;
        }
//        onCreateOffer();
//        progressBar.setVisibility(View.VISIBLE);
//        layout_create.setVisibility(View.GONE);
//        setProgressValue(100);
//        btnAccept.setText(getResources().getString(R.string.string_waiting));
//        btnAccept.setClickable(false);
//        checkOfferStatus();
    }

    private void checkOfferStatus() {
        mRef = mDatabase.getReference("deliveryoffer/");
        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot ds, @Nullable String s) {
                String key = ds.getKey();
                String status = ds.child("status").getValue(String.class);
                if(status == null)
                {
                    return;
                }
                if(key.equals(uniqueId) && status.equals("accepted")) {
                    progressBar.setVisibility(View.GONE);
                    layout_create.setVisibility(View.GONE);
                    txt_offering_rate.setVisibility(View.GONE);
                    btnAccept.setVisibility(View.VISIBLE);
                    time = Integer.parseInt(ds.child("time").getValue(String.class));
                    onCountdownTimer();
                    btn_driver_cancel.setVisibility(View.VISIBLE);
                    btn_change_price.setVisibility(View.VISIBLE);
                    isAccepted = true;
                    onCheckUnreadChat();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot ds, @Nullable String previousChildName) {
                String key = ds.getKey();
                String status = ds.child("status").getValue(String.class);
                if(status == null)
                {
                    return;
                }
                if(key.equals(uniqueId) && status.equals("accepted")) {
                    progressBar.setVisibility(View.GONE);
                    layout_create.setVisibility(View.GONE);
                    txt_offering_rate.setVisibility(View.GONE);
                    btnAccept.setVisibility(View.VISIBLE);
                    onCountdownTimer();
                    btn_driver_cancel.setVisibility(View.VISIBLE);
                    btn_change_price.setVisibility(View.VISIBLE);
                    isAccepted = true;
                    onCheckUnreadChat();
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String key = snapshot.getKey();
                String status = snapshot.child("status").getValue(String.class);
                if(key.equals(uniqueId)) {
                    progressBar.setVisibility(View.GONE);
                    txt_offering_rate.setVisibility(View.GONE);
                    btnAccept.setVisibility(View.VISIBLE);
                    if (!isAccepted) {
                        layout_create.setVisibility(View.VISIBLE);
                        btnBack.setVisibility(View.VISIBLE);
                        onShowAlert(getResources().getString(R.string.string_offer_rejected), true);
                    }
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

    private void onShowAlert(String msg, boolean back) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainDeliveryActivity.this);
                builder.setMessage(msg)
                        .setPositiveButton(getResources().getString(R.string.string_ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if(back) {
                                    Intent intent = new Intent(getApplicationContext(), OffersActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
                // Create the AlertDialog object and return it
                builder.create();
                if (!MainDeliveryActivity.this.isFinishing()) {
                    builder.show();
                }
            }
        });
    }

    private void onCreateOffer() {
        uniqueId = UUID.randomUUID().toString();
        mDatabase.getReference("deliveryoffer/"+uniqueId+"/driver").setValue(mAuth.getUid());
        mDatabase.getReference("deliveryoffer/"+uniqueId+"/price").setValue(String.valueOf(change_price));
        mDatabase.getReference("deliveryoffer/"+uniqueId+"/time").setValue(String.valueOf(time));
        mDatabase.getReference("deliveryoffer/"+uniqueId+"/driver_name").setValue(Common.getInstance().getUserName());
        mDatabase.getReference("deliveryoffer/"+uniqueId+"/driver_avatar").setValue(Common.getInstance().getAvatar());
        mDatabase.getReference("deliveryoffer/"+uniqueId+"/driver_brand").setValue(Common.getInstance().getBrand());
        mDatabase.getReference("deliveryoffer/"+uniqueId+"/driver_phone").setValue(Common.getInstance().getPhone_token());
        mDatabase.getReference("deliveryoffer/"+uniqueId+"/driverphone").setValue(Common.getInstance().getPhonenumber());
        mDatabase.getReference("deliveryoffer/"+uniqueId+"/ride").setValue(delivery_id);
        mDatabase.getReference("deliveryoffer/"+uniqueId+"/review").setValue(Common.getInstance().getReview());
        mDatabase.getReference("deliveryoffer/"+uniqueId+"/status").setValue("waiting");
        price = change_price;
    }

    private void onGetUserInfo() {
        mRef = mDatabase.getReference("user/" + mAuth.getUid() + "/avatar");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String driver_avatar_link = dataSnapshot.getValue(String.class);
                if (driver_avatar_link != null && driver_avatar_link != "") {
                    Common.getInstance().setAvatar(driver_avatar_link);
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

    private void setProgressValue(final int progress) {

        // set the progress
        progressBar.setProgress(progress);
        int val = progressBar.getProgress();
        // thread is used to change the progress value
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep( 200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(val <= 0) {
                    if(!isAccepted) {
                        removeOffer();
                    }
                } else {
                    setProgressValue(progress - 1);
                }
            }
        });
        thread.start();
    }

    private void removeOffer() {
        mDatabase.getReference("deliveryoffer/"+uniqueId).removeValue();
    }

    private void onGetDeliveryStatus() {
        mRef = mDatabase.getReference("delivery/" + delivery_id);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                onProgress(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void onRemoveTripPanel() {
        mRef = mDatabase.getReference("delivery/");
        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull @NotNull DataSnapshot snapshot) {
                if (delivery_id.equals(snapshot.getKey())){
                    onShowAlert(getResources().getString(R.string.cancel_delivery), true);
                }
            }

            @Override
            public void onChildMoved(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void onProgress(DataSnapshot ds) {
        bookingStatus = ds.child("status").getValue(String.class);
        if (bookingStatus != null) {
            userPhone = ds.child("phone").getValue(String.class);
            String passenger_uid = ds.child("user").getValue(String.class);
            Common.getInstance().setUser_uid(passenger_uid);
            String driver_token = ds.child("driver_phone").getValue(String.class);
            if(bookingStatus.equals("waiting")) {
                if(isAccepted) {
                    btnAccept.setText(getResources().getString(R.string.string_pickup));
                } else {
                    btnAccept.setText(getResources().getString(R.string.string_make_offer));
                }
                btnAccept.setClickable(true);
            } else if (bookingStatus.equals("accept")) {
                layout_change_price.setVisibility(View.VISIBLE);
                layout_accept_btn.setVisibility(View.VISIBLE);
                layout_communication.setVisibility(View.VISIBLE);
                btnWhatsapp.setVisibility(View.VISIBLE);
                btnAccept.setVisibility(View.VISIBLE);
                txtServicePrice.setText(String.valueOf(price) + "MXN");
                txtServicePrice.setVisibility(View.VISIBLE);
                btnBack.setVisibility(View.GONE);
                btnCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (ActivityCompat.checkSelfPermission(MainDeliveryActivity.this,
                                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
                        {
                            ActivityCompat.requestPermissions(MainDeliveryActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 1);

                        }else{
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + userPhone));
                            startActivity(intent);
                        }
                    }
                });
                btnChat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        unread = 0;
                        onSetUnread();
                        Intent intent = new Intent(getApplicationContext(), DriverChatActivity.class);
                        intent.putExtra("trip", "no");
                        startActivityForResult(intent, 0);
                    }
                });
                progressBar.setVisibility(View.GONE);
                layout_create.setVisibility(View.GONE);
                btnAccept.setText(getResources().getString(R.string.string_pickup));
                btnAccept.setClickable(true);
            } else if (bookingStatus.equals("pickup")) {
                btnAccept.setVisibility(View.GONE);
                txt_offering_rate.setText(getResources().getString(R.string.string_waiting_customer_dot));
                txt_offering_rate.setVisibility(View.VISIBLE);
            } else if (bookingStatus.equals("pay")) {
                txt_waiting_customr.setVisibility(View.VISIBLE);
                txt_offering_rate.setVisibility(View.GONE);
                btnAccept.setText(getResources().getString(R.string.string_end_trip));
                btnAccept.setClickable(true);
                btnAccept.setVisibility(View.VISIBLE);
            }
        }

        int change_offer = ds.child("price_offer").getValue(Integer.class) == null ? 0 : ds.child("price_offer").getValue(Integer.class);
        if(change_offer == 2 && !changedPrice) {
            changedPrice = true;
            onShowAlert(getResources().getString(R.string.string_accepted_change), false);
            price = change_price;
            txtServicePrice.setText(String.valueOf(price) + " MXN");
            txtServicePrice.setVisibility(View.VISIBLE);
            mDatabase.getReference("delivery/"+ delivery_id + "/price_offer").setValue(0);
            mDatabase.getReference("delivery/"+ delivery_id + "/change_price").setValue(-1);
            txtWaitingChange.setVisibility(View.GONE);
            btn_change_price.setVisibility(View.VISIBLE);
        } else if(change_offer == 3) {
            mDatabase.getReference("delivery/"+ delivery_id + "/price_offer").setValue(0);
            mDatabase.getReference("delivery/"+ delivery_id + "/change_price").setValue(-1);
            onShowAlert(getResources().getString(R.string.string_rejected_change), false);
            txtWaitingChange.setVisibility(View.GONE);
            btn_change_price.setVisibility(View.VISIBLE);
        }
    }

    private void bookingRide(){
        if(bookingStatus.equals("")) {
//            onCreateOffer();
//            progressBar.setVisibility(View.VISIBLE);
//            layout_create.setVisibility(View.GONE);
//            setProgressValue(100);
//            btnAccept.setText(getResources().getString(R.string.string_waiting));
//            btnAccept.setClickable(false);
//            checkOfferStatus();
        } else if (bookingStatus.equals("waiting")){
            onCreateOffer();
            txt_offering_rate.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            layout_create.setVisibility(View.GONE);
            setProgressValue(100);
            btnAccept.setVisibility(View.GONE);
            btnBack.setVisibility(View.GONE);
//            mDatabase.getReference("user/"+mAuth.getUid()+"/join").setValue("off");
//             mDatabase.getReference("delivery/"+delivery_id+"/status").setValue("accept");
        }else if (bookingStatus.equals("accept")){
//            String notify_text = String.format(getResources().getString(R.string.string_notify_driver_arrive));
//            sendNotification(notify_text);
            txt_driver_timer.setVisibility(View.VISIBLE);
            mDatabase.getReference("delivery/"+delivery_id+"/status").setValue("pickup");
        }else if (bookingStatus.equals("pickup")){
//            mDatabase.getReference("ride/"+uniqueId+"/status").setValue("pay");
        }else if (bookingStatus.equals("pay")){
            onShowEndTripAlert();
        }else if (bookingStatus.equals("paid")){
//            if(paytype.equals("cash")) {
//                mDatabase.getReference("user/"+mAuth.getUid()+"/join").setValue("on");
//                mDatabase.getReference("ride/"+ride_uuid+"/status").setValue("complete");
//                Intent intent=new Intent(MainDriverActivity.this, DriverReview.class);
//                startActivity(intent);
//                finish();
//            } else {
//                mDatabase.getReference("user/"+mAuth.getUid()+"/join").setValue("off");
//                mDatabase.getReference("ride/"+ride_uuid+"/status").setValue("process");
//            }
////            _layoutConfirm.setVisibility(View.GONE);
        } else if(bookingStatus.equals("process")) {
//            onShowEndTripAlert();
        } else if(bookingStatus.equals("done")) {
//            mDatabase.getReference("user/"+mAuth.getUid()+"/join").setValue("on");
//            mDatabase.getReference("ride/"+ride_uuid+"/status").setValue("complete");
        } else if (bookingStatus.equals("complete")){
//            mDatabase.getReference("user/"+mAuth.getUid()+"/join").setValue("on");
//            _layoutConfirm.setVisibility(View.VISIBLE);
//            Intent intent=new Intent(MainDriverActivity.this, DriverReview.class);
//            startActivity(intent);
//            finish();
        }
    }

    private void onChangePrice() {
        if(change_price < 35) {
            onShowAlert("Price must be 35MXN or higher.", false);
            return;
        }
        txtWaitingChange.setVisibility(View.VISIBLE);
        btn_change_price.setVisibility(View.GONE);
        changedPrice = false;
        mDatabase.getReference("delivery/"+delivery_id+"/price_offer").setValue(1);
        mDatabase.getReference("delivery/"+delivery_id+"/change_price").setValue(change_price);
    }

    private void AddConstantTextInEditText(EditText edt, String text) {

        Selection.setSelection(edt.getText(), edt.getText().length());
        edt.setText(text);

        edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().endsWith(text)){
                    Selection.setSelection(edt.getText(), edt.getText().length());
                    edt.setText(text);

                }
                String txt = edt.getText().toString();
                txt = txt.replaceAll("\\D+","");
                if(txt != null && !txt.equals("")) {
                    change_price = Integer.parseInt(txt);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
//                if(!s.toString().endsWith(text)){
//                    Selection.setSelection(edt.getText(), edt.getText().length());
//                    edt.setText(text);
//                    String txt = edt.getText().toString();
//                    txt = txt.replaceAll("\\D+","");
//                    if(txt != null && !txt.equals("")) {
//                        change_price = Integer.parseInt(txt);
//                    }
//                }
            }
        });
    }

    private void onCountdownTimer() {
        txt_driver_timer.setVisibility(View.VISIBLE);
        txt_driver_timer.setText(String.format("%02d", time) + " : " + "00");
        countDownTimer = new CountDownTimer(time * 60 * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                String minute = String.format("%02d", (millisUntilFinished / 1000) / 60);
                String second =  String.format("%02d", (millisUntilFinished / 1000) % 60);
                txt_driver_timer.setText(minute + " : " + second);
            }

            public void onFinish() {
            }

        }.start();
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
//                sendNotification(getResources().getString(R.string.string_notify_driver_cancel));
                mDatabase.getReference("delivery/"+delivery_id).removeValue();
//                onSaveTriptoDB(0);
                Intent intent=new Intent(MainDeliveryActivity.this, OffersActivity.class);
                startActivity(intent);
                finish();
            }
        });

        reason2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.cancel();
//                sendNotification(getResources().getString(R.string.string_notify_driver_cancel));
                mDatabase.getReference("delivery/"+delivery_id).removeValue();
//                onSaveTriptoDB(0);
                Intent intent=new Intent(MainDeliveryActivity.this, OffersActivity.class);
                startActivity(intent);
                finish();
            }
        });

        reason3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.cancel();
//                sendNotification(getResources().getString(R.string.string_notify_driver_cancel));
                mDatabase.getReference("delivery/"+delivery_id).removeValue();
//                onSaveTriptoDB(0);
                Intent intent=new Intent(MainDeliveryActivity.this, OffersActivity.class);
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
//                sendNotification(getResources().getString(R.string.string_notify_driver_cancel));
                mDatabase.getReference("delivery/"+delivery_id).removeValue();
//                onSaveTriptoDB(0);
                Intent intent=new Intent(MainDeliveryActivity.this, OffersActivity.class);
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

    private void onShowEndTripAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainDeliveryActivity.this);
        builder.setMessage(getResources().getString(R.string.string_trip_complete_question))
                .setPositiveButton(getResources().getString(R.string.string_yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mDatabase.getReference("delivery/"+delivery_id+"/status").setValue("complete");
                        mDatabase.getReference("user/"+mAuth.getUid()+"/join").setValue("on");
                        onRemoveChat();
                        onSaveTriptoDB(1);
                        Intent intent=new Intent(MainDeliveryActivity.this, DriverReview.class);
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
        if (!MainDeliveryActivity.this.isFinishing()) {
            builder.show();
        }
    }

    private void onInCompleteTrip() {
        LayoutInflater factory = LayoutInflater.from(MainDeliveryActivity.this);
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

        AlertDialog.Builder builder = new AlertDialog.Builder(MainDeliveryActivity.this);
        builder.setPositiveButton(getResources().getString(R.string.string_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                reason_comment = other_reason.getText().toString();
                if(reason == 2 && reason_comment.equals("")) {
//                    other_reason.setError(getResources().getString(R.string.string_enter_reason));
                    Toast.makeText(MainDeliveryActivity.this, getResources().getString(R.string.string_enter_reason), Toast.LENGTH_LONG ).show();
                    return;
                } else {
                    mDatabase.getReference("delivery/"+delivery_id).removeValue();
//                    onSaveTriptoDB(0);
                    Intent intent=new Intent(MainDeliveryActivity.this, OffersActivity.class);
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

    private void onCheckUnreadChat() {
        String user_id;
        String agency_id;
        user_id = Common.getInstance().getUser_uid();
        agency_id = Common.getInstance().getDriver_uid();

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
        mDatabase.getReference("chat/agency/"+ mAuth.getUid() + "/" + user_id).removeValue();
    }

    @Override
    public void onBackPressed() {

    }

    private void onSaveTriptoDB(int status) {
        String start = "";
        if(deliveryRequest.getType() == 0) {
            start = deliveryRequest.getServicePayment().getPickUp();
        } else if(deliveryRequest.getType() == 1) {
            start = deliveryRequest.getRestaurant().getPickUp();
        } else if(deliveryRequest.getType() == 2) {
            start = deliveryRequest.getSupermarket().getPickUp();
        }
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:s");
        String str_date = inputFormat.format(date);

        JsonObject json = new JsonObject();
        json.addProperty("uid", Common.getInstance().getUser_uid());
        json.addProperty("driver_uid", mAuth.getUid());
        json.addProperty("start", start);
        json.addProperty("price", price);
        json.addProperty("status", status);
        json.addProperty("date", str_date);
        json.addProperty("type", deliveryRequest.getType() + 1);

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
}