package com.mobiledevteam.kuiki;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.mobiledevteam.kuiki.Adapter.TripHistory;
import com.mobiledevteam.kuiki.Adapter.TripHistoryAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DriverTripHistoryActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;

    private ImageView ivHeaderPhoto;
    private TextView ivHeadName;
    private DrawerLayout mDrawer;
    private NavigationView nvDrawer;
    private LinearLayout btn_profile;
    private LinearLayout btn_offers;
    private LinearLayout btn_setting;
    private LinearLayout btn_safety;
    private LinearLayout btn_help;
    private LinearLayout btn_support;
    private Button btn_mode;
    private View headerLayout;
    private NavigationView navigationView;
    private Button btn_menu;
    private GridView trip_grid;

    ArrayList<TripHistory> mAllTripList = new ArrayList<>();
    TripHistoryAdapter adapter_event;
    private String type = "user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_trip_history);

        Intent intent = getIntent();

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
//        setupDrawerContent(nvDrawer);
        navigationView = (NavigationView) findViewById(R.id.nvView);
        headerLayout = navigationView.inflateHeaderView(R.layout.nav_header);
        ivHeaderPhoto = headerLayout.findViewById(R.id.avatar);
        ivHeadName = headerLayout.findViewById(R.id.user_name);
        btn_profile = (LinearLayout) headerLayout.findViewById(R.id.btn_go_profile);
        btn_offers = (LinearLayout) headerLayout.findViewById(R.id.btn_user_trip);
        btn_safety = (LinearLayout) headerLayout.findViewById(R.id.btn_driver_privacy);
        btn_setting = (LinearLayout) headerLayout.findViewById(R.id.btn_driver_setting);
        btn_help = (LinearLayout) headerLayout.findViewById(R.id.btn_driver_help);
        btn_support = (LinearLayout) headerLayout.findViewById(R.id.btn_driver_support);
        btn_mode = (Button)findViewById(R.id.btn_passenger_mode);
        btn_mode = (Button) findViewById(R.id.btn_passenger_mode);
        btn_menu = (Button) findViewById(R.id.btn_menu);
        trip_grid = (GridView) findViewById(R.id.trip_grid);

        setView();
        onSetMenuEvent();
        onGetTripHistory();
    }

    private void setView() {
        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawer.openDrawer(nvDrawer);
            }
        });

        ivHeadName.setText(Common.getInstance().getUserName());
        String url = Common.getInstance().getAvatar();
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
            Toast.makeText(DriverTripHistoryActivity.this, getResources().getString(R.string.string_offline), Toast.LENGTH_LONG ).show();
        }else{
            mDatabase.getReference("user/"+mAuth.getUid()+"/type").setValue("user");
            mDatabase.getReference("user/"+mAuth.getUid()+"/join").setValue("off");
            Intent intent=new Intent(getBaseContext(), HomeActivity.class);
            startActivity(intent);
            finish();
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

    private void onGetTripHistory() {
        JsonObject json = new JsonObject();
        json.addProperty("uid", mAuth.getUid());
        json.addProperty("type", 1);
        try {
            Ion.with(this)
                    .load(Common.getInstance().getBaseURL()+"api/gettriphistory")
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (result != null) {
                                String res = result.get("status").getAsString();
                                if (res.equals("ok")) {
                                    JsonArray arr = result.getAsJsonArray("result");
                                    for(int i = 0; i < arr.size(); i++) {
                                        JsonElement item = arr.get(i);
                                        JsonObject object = item.getAsJsonObject();
                                        int type = object.get("type").getAsInt();
                                        String date = "";
                                        String start = "";
                                        String end = "";
                                        String price = "";
                                        int status = 0;
                                        TripHistory trip = new TripHistory();
                                        if(type == 0) {
                                            date = object.get("date").getAsString();
                                            start = object.get("start").getAsString();
                                            end = object.get("end").getAsString();
                                            price = object.get("price").getAsString();
                                            status = object.get("status").getAsInt();
                                            trip.setDate(date);
                                            trip.setStart(start);
                                            trip.setEnd(end);
                                            trip.setPrice(price);
                                            trip.setStatus(status);
                                            trip.setType(type);
                                            if(status == 0) {
                                                trip.setStatusTxt(getResources().getString(R.string.string_trip_canceled));
                                            } else {
                                                trip.setStatusTxt(getResources().getString(R.string.string_trip_completed));
                                            }
                                        } else {
                                            date = object.get("date").getAsString();
                                            start = object.get("start").getAsString();
                                            price = object.get("price").getAsString();
                                            status = object.get("status").getAsInt();
                                            trip.setDate(date);
                                            trip.setStart(start);
                                            trip.setPrice(price + "MXN");
                                            trip.setStatus(status);
                                            trip.setType(type);
                                            if(status == 0) {
                                                trip.setStatusTxt(getResources().getString(R.string.string_trip_canceled));
                                            } else {
                                                trip.setStatusTxt(getResources().getString(R.string.string_trip_completed));
                                            }
                                        }
                                        mAllTripList.add(trip);
                                    }
                                    initView();
                                } else {
                                }
                            } else {
                            }
                        }
                    });
        }catch(Exception e){
        }
    }

    private void initView() {
        if(mAllTripList.size() == 0 ) {
            return;
        } else {
        }
        adapter_event = new TripHistoryAdapter(getBaseContext(), mAllTripList);
        trip_grid.setAdapter(adapter_event);
    }
}