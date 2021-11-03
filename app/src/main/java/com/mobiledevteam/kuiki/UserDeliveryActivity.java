package com.mobiledevteam.kuiki;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.mobiledevteam.kuiki.Adapter.DeliveryOffer;
import com.mobiledevteam.kuiki.Adapter.DriverOffer;
import com.mobiledevteam.kuiki.Adapter.PageViewAdapter;
import com.mobiledevteam.kuiki.data.RestaurantData;
import com.mobiledevteam.kuiki.data.ServicePaymentData;
import com.mobiledevteam.kuiki.data.SupermarketData;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import co.intentservice.chatui.models.ChatMessage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class UserDeliveryActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private DatabaseReference mRideRef;
    private DatabaseReference myMsgRef;
    private ChildEventListener _childListener;
    private SupportMapFragment mapFragment;
    private MarkerOptions start_markerOptions;
    private MarkerOptions end_markerOptions;
    private MarkerOptions car_markerOptions;
    private Bitmap start_smallMarker;
    private Bitmap end_smallMarker;
    private Bitmap car_smallMarker;
    private Marker carMarker = null;

    private TextView _driverName;
    private ImageView img_driver_avatar;
    private TextView _startAddress;
    private TextView _endAddress;
    private TextView _brand;
    private TextView _carNumber;
    private Button _callPhone;
    private Button _whatsapp;
    private Button _btnBook;
    private TextView _btnCancel;
    private LinearLayout btn_passenger_call;
    private LinearLayout layout_passenger_notify;
    private TextView passenger_notify;
    private TextView txt_service_time;
    private TextView txt_service_cost;
    private LinearLayout layout_service_time;
    private TextView txt_counter_badge;
    private TextView txt_driver_review;

    private int serviceType;
    private ServicePaymentData servicePayment;
    private RestaurantData restaurant;
    private SupermarketData supermarket;
    private String key = "";
    private DeliveryOffer accepted_offer;
    private String bookingStatus="none";

    private CountDownTimer countDownTimer;
    private int unread;

    private OkHttpClient httpClient = new OkHttpClient();
    private int slideCurrentItem=0;
    private ArrayList<String> mSliderImages = new ArrayList<>();
    private ViewPager _clinicSlider;
    private String uniqueId;
    private boolean isNew = false;

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d("selected:", String.valueOf(slideCurrentItem));
            _clinicSlider.setCurrentItem(slideCurrentItem);
            slideCurrentItem = slideCurrentItem + 1;
            if (mSliderImages.size() > slideCurrentItem) {

            } else {
                slideCurrentItem = 0;
            }
            timerHandler.postDelayed(this, 5000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_delivery);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        serviceType = Common.getInstance().getServiceType();

        Intent intent = getIntent();
        String status = intent.getStringExtra("create");
        if(status.equals("yes")) {
            isNew = true;
        } else {
            isNew = false;
            uniqueId = status;
        }

        _driverName = (TextView)findViewById(R.id.txt_deliveryname);
        _brand = (TextView) findViewById(R.id.car_brand);
        _carNumber = (TextView)findViewById(R.id.txt_carnumber);
        _startAddress = (TextView)findViewById(R.id.ride_start_address);
        _endAddress = (TextView)findViewById(R.id.end_address);
        _callPhone = (Button)findViewById(R.id.img_phone);
        _whatsapp = (Button) findViewById(R.id.btn_driver_whatsapp);
        _btnBook = (Button)findViewById(R.id.btn_booking);
        _btnCancel = (TextView)findViewById(R.id.btn_passenger_cancel);
        btn_passenger_call = (LinearLayout) findViewById(R.id.btn_passenger_call);
        img_driver_avatar = (ImageView) findViewById(R.id.img_driver_avatar);
        layout_passenger_notify = (LinearLayout) findViewById(R.id.layout_passenger_notify);
        passenger_notify = (TextView) findViewById(R.id.passenger_notify);
        txt_service_cost = (TextView) findViewById(R.id.txt_service_cost);
        txt_service_time = (TextView) findViewById(R.id.txt_service_time);
        layout_service_time = (LinearLayout) findViewById(R.id.layout_service_time);
        txt_counter_badge = (TextView) findViewById(R.id.txt_counter_badge);
        txt_driver_review = (TextView) findViewById(R.id.txt_driver_review);
        _clinicSlider = (ViewPager) findViewById(R.id.slider_clinic);

        switch (serviceType) {
            case 0:
                servicePayment = Common.getInstance().getServicePayment();
                break;
            case 1:
                restaurant = Common.getInstance().getRestaurant();
                break;
            case 2:
                supermarket = Common.getInstance().getSupermarket();
                break;
            case 3:
                break;
        }

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
//                    sendNotification(getResources().getString(R.string.string_notify_passenger_cancel));
                    mDatabase.getReference("delivery/"+accepted_offer.getRideid()).removeValue();
                    Intent intent = new Intent(UserDeliveryActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    showCancelDialog();
                }
            }
        });

        if(isNew) {
            onSetSomeInfo();
        } else {
            onGetExistTripInfo();
        }

        getSliderData();
    }

    @Override
    public void onBackPressed() {

    }

    private void onSetSomeInfo() {
        accepted_offer = Common.getInstance().getDeliveryRequest();
        Picasso.get().load(accepted_offer.getmImage()).into(img_driver_avatar);
        _driverName.setText(accepted_offer.getName());
        String accepted_price = accepted_offer.getPrice();
        String accepted_time = accepted_offer.getTime();
        txt_service_cost.setText(accepted_price + "MXN");
        onCountdownTimer(accepted_time);
        String notify_text = String.format(getResources().getString(R.string.string_passenger_accept), accepted_price, accepted_time);
        _brand.setText(accepted_offer.getBrand());
        txt_driver_review.setText(accepted_offer.getReview());

        mRef = mDatabase.getReference("user/"+accepted_offer.getUid());
        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.getKey().equals("plate")){
                    String plate = dataSnapshot.getValue().toString();
                    _carNumber.setVisibility(View.VISIBLE);
                    _carNumber.setText(plate);
                }
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

        mRideRef = mDatabase.getReference("delivery/"+accepted_offer.getRideid());
        mRideRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bookingStatus = dataSnapshot.child("status").getValue(String.class);
                if (bookingStatus == null){
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    if (bookingStatus.equals("waiting")){
                        _btnBook.setText(getResources().getString(R.string.string_waiting));
                        _btnBook.setEnabled(false);
                    }else if (bookingStatus.equals("accept")){
                        _callPhone.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (ActivityCompat.checkSelfPermission(UserDeliveryActivity.this,
                                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
                                {
                                    ActivityCompat.requestPermissions(UserDeliveryActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 1);

                                }else{
                                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Common.getInstance().getPhonenumber()));
                                    startActivity(intent);
                                }
                            }
                        });

                        _whatsapp.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                                startActivityForResult(intent, 0);
                            }
                        });
                        _btnBook.setVisibility(View.GONE);
                        _btnBook.setEnabled(false);
                        layout_passenger_notify.setVisibility(View.VISIBLE);
                        passenger_notify.setText(getResources().getString(R.string.string_delivery_way));
                        onCheckUnreadChat();
                    }else if (bookingStatus.equals("pickup")){
                        _btnBook.setVisibility(View.VISIBLE);
                        _btnBook.setText(getResources().getString(R.string.string_passenger_understood));
                        _btnBook.setVisibility(View.VISIBLE);
                        _btnBook.setEnabled(true);
                        layout_service_time.setVisibility(View.GONE);
                        layout_passenger_notify.setVisibility(View.VISIBLE);
                        passenger_notify.setText(getResources().getString(R.string.string_delivery_arrive));
                    }else if (bookingStatus.equals("pay")){
                        _btnBook.setVisibility(View.GONE);
                        passenger_notify.setText(getResources().getString(R.string.string_delivery_wait));
                        layout_passenger_notify.setVisibility(View.VISIBLE);
                    } else if (bookingStatus.equals("complete")){
                        Intent intent = new Intent(getApplicationContext(), PassengerReview.class);
                        intent.putExtra("ride_id", accepted_offer.getRideid());
                        startActivity(intent);
                        finish();
                    }
                }

                int change_offer = dataSnapshot.child("price_offer").getValue(Integer.class) == null ? 0 : dataSnapshot.child("price_offer").getValue(Integer.class);
                int change_price = dataSnapshot.child("change_price").getValue(Integer.class) == null ? -1 : dataSnapshot.child("change_price").getValue(Integer.class);
                int accepted = Integer.parseInt(accepted_offer.getPrice().replace("MXN", ""));
                if(change_offer == 1 && change_price != -1 && accepted != change_price) {
                    onShowPriceAccept(change_price);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void onGetExistTripInfo() {
        mRef = FirebaseDatabase.getInstance().getReference().child("deliveryoffer/");
        mRef.orderByChild("ride").equalTo(uniqueId).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for(DataSnapshot ds : snapshot.getChildren()) {
                        String key = ds.getKey();
                        onGetOffer(key);
                    }
                } else {
                    Intent intent = new Intent(UserDeliveryActivity.this, HomeActivity.class);
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
        mRef = mDatabase.getReference("deliveryoffer/" + key);
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
                    String status = ds.child("status").getValue(String.class);
                    String driverphone = ds.child("driverphone").getValue(String.class);
                    String review = ds.child("review").getValue(String.class);
                    if(ride_id != null && driver_id != null && driver_name != null && driver_avatar != null && driver_brand != null && driver_price != null && driver_time!= null && driverphone != null && status != null && driver_phone != null && review != null) {
                        String mId = ds.getKey();

                        DeliveryOffer offer = new DeliveryOffer();
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
                        offer.setPrice(String.valueOf(driver_price));
                        offer.setTime(String.valueOf(driver_time));
                        offer.setPhone(driverphone);
                        offer.setReview(review);

                        Common.getInstance().setDeliveryRequest(offer);
                        onSetSomeInfo();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void onShowPriceAccept(int change_price) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.ThemeOverlay_App_BottomSheetDialog);
        bottomSheetDialog.setContentView(R.layout.delivery_change_price);
        bottomSheetDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        bottomSheetDialog.setCancelable(false);

        TextView txtChangePrice = (TextView) bottomSheetDialog.findViewById(R.id.change_price);
        Button btn_agree = (Button) bottomSheetDialog.findViewById(R.id.btn_agree);
        Button cancel = (Button) bottomSheetDialog.findViewById(R.id.btn_cancel_dialog);
        txtChangePrice.setText(String.format(getResources().getString(R.string.string_title_change_price), String.valueOf(change_price)));

        btn_agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accepted_offer.setPrice(String.valueOf(change_price));
                mDatabase.getReference("delivery/"+accepted_offer.getRideid() + "/price").setValue(change_price);
                mDatabase.getReference("delivery/"+accepted_offer.getRideid() + "/price_offer").setValue(2);
                 txt_service_cost.setText(String.valueOf(change_price) + "MXN");
                bottomSheetDialog.cancel();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.getReference("delivery/"+accepted_offer.getRideid() + "/price_offer").setValue(3);
                bottomSheetDialog.cancel();
            }
        });
        bottomSheetDialog.show();
    }

    private void bookingRide(){
        if(bookingStatus.equals("none")){
        }else if (bookingStatus.equals("waiting")){
        }else if (bookingStatus.equals("accept")){
            mDatabase.getReference("delivery/"+accepted_offer.getRideid()+"/status").setValue("pay");
        }else if (bookingStatus.equals("pickup")){
            mDatabase.getReference("delivery/"+accepted_offer.getRideid()+"/status").setValue("pay");
//            sendNotification(getResources().getString(R.string.string_notify_passenger_going));
        } else if (bookingStatus.equals("complete")){
//            onSaveTriptoDB(1);
            Intent intent = new Intent(UserDeliveryActivity.this, PassengerReview.class);
            intent.putExtra("ride_id", accepted_offer.getRideid());
            startActivity(intent);
            finish();
        }
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
//                mDatabase.getReference("delivery/"+accepted_offer.getRideid()).removeValue();
//                Intent intent = new Intent(UserDeliveryActivity.this, HomeActivity.class);
//                startActivity(intent);
//                finish();
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
                mDatabase.getReference("delivery/"+accepted_offer.getRideid()).removeValue();
                onSaveTriptoDB(0);
                Intent intent = new Intent(UserDeliveryActivity.this, HomeActivity.class);
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
                mDatabase.getReference("delivery/"+accepted_offer.getRideid()).removeValue();
                onSaveTriptoDB(0);
                Intent intent = new Intent(UserDeliveryActivity.this, HomeActivity.class);
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

    private void onSaveTriptoDB(int status) {
        String start = "";
        switch (serviceType) {
            case 0:
                start = servicePayment.getPickUp();
                break;
            case 1:
                start = restaurant.getPickUp();
                break;
            case 2:
                start = supermarket.getPickUp();
                break;
            case 3:
                break;
        }
        String price = accepted_offer.getPrice();
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:s");
        String str_date = inputFormat.format(date);

        JsonObject json = new JsonObject();
        json.addProperty("uid", mAuth.getUid());
        json.addProperty("driver_uid", Common.getInstance().getDriver_uid());
        json.addProperty("start", start);
        json.addProperty("price", price);
        json.addProperty("status", status);
        json.addProperty("date", str_date);
        json.addProperty("type", serviceType + 1);

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

    private void onCountdownTimer(String txtTime) {
        int time = Integer.parseInt(txtTime);
        txt_service_time.setVisibility(View.VISIBLE);
        txt_service_time.setText(String.format("%02d", time) + " : " + "00");
        countDownTimer = new CountDownTimer(time * 60 * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                String minute = String.format("%02d", (millisUntilFinished / 1000) / 60);
                String second =  String.format("%02d", (millisUntilFinished / 1000) % 60);
                txt_service_time.setText(minute + " : " + second);
            }

            public void onFinish() {
            }

        }.start();
    }

    private void onCheckUnreadChat() {
        String user_id;
        String agency_id;
        user_id = Common.getInstance().getUser_uid();
        agency_id = Common.getInstance().getDriver_uid();

        myMsgRef = mDatabase.getReference("chat/user/" + user_id + "/" + agency_id);
        _childListener = myMsgRef.addChildEventListener(new ChildEventListener() {
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

    private void initSlider() {
        PageViewAdapter adapter = new PageViewAdapter(this, mSliderImages);
        _clinicSlider.setAdapter(adapter);
    }
}