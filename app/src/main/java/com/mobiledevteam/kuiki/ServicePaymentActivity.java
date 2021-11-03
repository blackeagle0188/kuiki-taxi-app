package com.mobiledevteam.kuiki;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
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
import com.mobiledevteam.kuiki.Adapter.PageViewAdapter;
import com.mobiledevteam.kuiki.data.ServicePaymentData;

import java.util.ArrayList;

import static java.util.Locale.getDefault;

public class ServicePaymentActivity extends AppCompatActivity {

    private Button service_company;
    private Button service_number;
    private Button service_address;
    private Button btnPlaceOrder;

    private String serviceName = "";
    private String serviceNumber = "";
    private String serviceAddress = "";

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
    private ImageView ivHeaderPhoto;
    private TextView ivHeadName;
    private Button btn_menu;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;

    private int slideCurrentItem=0;
    private ArrayList<String> mSliderImages = new ArrayList<>();
    private ViewPager _clinicSlider;

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
        setContentView(R.layout.activity_service_payment);

        mAuth = FirebaseAuth.getInstance();

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
        btn_menu = (Button) findViewById(R.id.btn_menu);
        _clinicSlider = (ViewPager) findViewById(R.id.slider_clinic);

        service_company = (Button) findViewById(R.id.service_company);
        service_number = (Button) findViewById(R.id.service_number);
        service_address = (Button) findViewById(R.id.service_address);
        btnPlaceOrder = (Button) findViewById(R.id.place_service_order);

        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawer.openDrawer(nvDrawer);
            }
        });

        service_company.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSericePayment();
            }
        });

        service_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSericeNumber();
            }
        });

        service_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSericeAddress();
            }
        });

        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPlaceOrder();
            }
        });

        onSetMenuEvent();
        getSliderData();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void onSericePayment() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.ThemeOverlay_App_BottomSheetDialog);
        bottomSheetDialog.setContentView(R.layout.service_name);
        bottomSheetDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        bottomSheetDialog.setCancelable(false);

        EditText name = (EditText) bottomSheetDialog.findViewById(R.id.service_name);
        Button btn_agree = (Button) bottomSheetDialog.findViewById(R.id.btn_dont_want);
        Button cancel = (Button) bottomSheetDialog.findViewById(R.id.btn_cancel_dialog);
        name.setText(serviceName);

        btn_agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serviceName = name.getText().toString();
                if(!serviceName.equals("")) {
                    service_company.setText(serviceName);
                }
                bottomSheetDialog.cancel();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.cancel();
            }
        });
        bottomSheetDialog.show();
    }

    private void onSericeNumber() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.ThemeOverlay_App_BottomSheetDialog);
        bottomSheetDialog.setContentView(R.layout.service_number);
        bottomSheetDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        bottomSheetDialog.setCancelable(false);

        EditText number = (EditText) bottomSheetDialog.findViewById(R.id.service_number);
        Button btn_agree = (Button) bottomSheetDialog.findViewById(R.id.btn_dont_want);
        Button cancel = (Button) bottomSheetDialog.findViewById(R.id.btn_cancel_dialog);
        number.setText(serviceNumber);

        btn_agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serviceNumber = number.getText().toString();
                if(!serviceNumber.equals("")) {
                    service_number.setText(serviceNumber);
                }
                bottomSheetDialog.cancel();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.cancel();
            }
        });
        bottomSheetDialog.show();
    }

    private void onSericeAddress() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.ThemeOverlay_App_BottomSheetDialog);
        bottomSheetDialog.setContentView(R.layout.service_address);
        bottomSheetDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        bottomSheetDialog.setCancelable(false);

        EditText address = (EditText) bottomSheetDialog.findViewById(R.id.service_address);
        Button btn_agree = (Button) bottomSheetDialog.findViewById(R.id.btn_dont_want);
        Button cancel = (Button) bottomSheetDialog.findViewById(R.id.btn_cancel_dialog);
        address.setText(serviceAddress);

        btn_agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serviceAddress = address.getText().toString();
                if(!serviceAddress.equals("")) {
                    service_address.setText(serviceAddress);
                }
                bottomSheetDialog.cancel();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.cancel();
            }
        });
        bottomSheetDialog.show();
    }

    private void onPlaceOrder() {

        if(serviceName.equals("")) {
            Toast.makeText(getApplicationContext(), "Enter service company", Toast.LENGTH_LONG).show();
            return;
        } else if (serviceNumber.equals("")) {
            Toast.makeText(getApplicationContext(), "Enter service number", Toast.LENGTH_LONG).show();
            return;
        } else if(serviceAddress.equals("")) {
            Toast.makeText(getApplicationContext(), "Enter address", Toast.LENGTH_LONG).show();
            return;
        }

        ServicePaymentData servicePayment = new ServicePaymentData();
        servicePayment.setServiceCompany(serviceName);
        servicePayment.setServiceNumber(serviceNumber);
        servicePayment.setPickUp(serviceAddress);
        Common.getInstance().setServicePayment(servicePayment);
        Common.getInstance().setServiceType(0);

        Intent intent = new Intent(getApplicationContext(), FindDeliveryActivity.class);
        intent.putExtra("create", "yes");
        startActivity(intent);
        finish();
    }

    private void onSetMenuEvent() {
        btn_go_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ServicePaymentActivity.this, UserProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btn_user_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ServicePaymentActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btn_user_privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ServicePaymentActivity.this, ProtectionActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btn_user_ride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ServicePaymentActivity.this, TripHistoryActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btn_user_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ServicePaymentActivity.this, UserSettingActivity.class);
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
                    Intent intent = new Intent(ServicePaymentActivity.this, OffersActivity.class);
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
                                        Intent intent = new Intent(ServicePaymentActivity.this, VerifyActivity.class);
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