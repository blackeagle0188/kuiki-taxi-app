package com.mobiledevteam.kuiki;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
//import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;

import java.util.Arrays;

public class PassengerReview extends AppCompatActivity {
    ImageView img_profile;
    TextView txt_name;
    RatingBar rbar_star;
    CheckBox chk_option1;
    CheckBox chk_option2;
    CheckBox chk_option3;
    CheckBox chk_option4;
    EditText txt_comment;
    Button btn_submit;
    Button btn_cancel;

    ImageView ivHeaderPhoto;
    TextView ivHeadName;
    private Toolbar toolbar;
    private DrawerLayout mDrawer;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;
    private LinearLayout btn_go_profile;
    private LinearLayout btn_user_home;
    private LinearLayout btn_user_ride;
    private LinearLayout btn_user_privacy;
    private LinearLayout btn_user_setting;
    private LinearLayout btn_user_help;
    private LinearLayout btn_user_support;
    private Button btn_mode;
    private Button btn_menu;

    private String str_uid;
    private String url_profile;
    private int[] options = new int[4];
    private String ride_id = "";

    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    DatabaseReference mRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_review);

        img_profile = (ImageView) findViewById(R.id.review_driver_profile);
        txt_name = (TextView) findViewById(R.id.review_driver_name);
        rbar_star = (RatingBar) findViewById(R.id.star_user);
        chk_option1 = (CheckBox) findViewById(R.id.user_option1);
        chk_option2 = (CheckBox) findViewById(R.id.user_option2);
        chk_option3 = (CheckBox) findViewById(R.id.user_option3);
        chk_option4 = (CheckBox) findViewById(R.id.user_option4);
        txt_comment = (EditText) findViewById(R.id.txt_user_comment);
        btn_submit = (Button) findViewById(R.id.btn_user_submit);
        btn_cancel = (Button) findViewById(R.id.btn_user_cancel);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        ride_id = getIntent().getStringExtra("ride_id");

        str_uid = Common.getInstance().getDriver_uid();

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();
        drawerToggle.setDrawerIndicatorEnabled(true);
        mDrawer.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        setupDrawerContent(nvDrawer);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nvView);
        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header);
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

        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawer.openDrawer(nvDrawer);
            }
        });

        onGetUserInfo();
        onSetMenuEvent();
        onGetDriverInfo();
        onSetButtonEvent();
    }

    private void onSetButtonEvent() {
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.getReference("ride/"+ride_id).removeValue();
                Float star = rbar_star.getRating();
                for(int i = 0; i < options.length; i++) {
                    options[i] = 0;
                }
                if(chk_option1.isChecked()) {
                    options[0] = 1;
                }
                if(chk_option2.isChecked()) {
                    options[1] = 1;
                }
                if(chk_option3.isChecked()) {
                    options[2] = 1;
                }
                if(chk_option4.isChecked()) {
                    options[3] = 1;
                }
                String str_options = Arrays.toString(options).replace("[", "").replace("]", "").replaceAll("\\s", "");
                String comment = txt_comment.getText().toString();
                onSubmitReview(star, str_options, comment);
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.getReference("ride/"+ride_id).removeValue();
                Intent intent = new Intent(PassengerReview.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void onGetDriverInfo() {
        mRef = mDatabase.getReference("user/" + str_uid + "/avatar");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String avatar_link = dataSnapshot.getValue(String.class);
                if (avatar_link != null && avatar_link != "") {
                    url_profile = Common.getInstance().getBaseURL() + "backend/" + avatar_link;
                    Picasso.get().load(url_profile).into(img_profile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mRef = mDatabase.getReference("user/" + str_uid + "/username");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String user_name = dataSnapshot.getValue(String.class);
                if (user_name != null && user_name != "") {
                    txt_name.setText(user_name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void onSubmitReview(Float star, String option, String comment) {
        JsonObject json = new JsonObject();
        json.addProperty("uid", str_uid);
        json.addProperty("star", star);
        json.addProperty("options", option);
        json.addProperty("comment", comment);
        Ion.with(this)
                .load(Common.getInstance().getBaseURL()+"api/add_user_review")
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (result != null) {
                            String res = result.get("status").getAsString();
                            if(res.equals("ok")) {
                                onShowAlert(getResources().getString(R.string.string_review_submit), true);
                            } else {
                            }
                        } else {
                        }
                    }
                });
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        return true;
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
    }

    private void onLocaAvatar(String url) {
        Picasso.get().load(Common.getInstance().getBaseURL() + "backend/" + url).into(ivHeaderPhoto);
    }

    private void onSetMenuEvent() {
        btn_go_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(PassengerReview.this, UserProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btn_user_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(PassengerReview.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btn_user_ride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(PassengerReview.this, TripHistoryActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btn_user_privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(PassengerReview.this, ProtectionActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btn_user_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(PassengerReview.this, UserSettingActivity.class);
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
//                    Common.getInstance().setPhone_token(phone_token);
                    mDatabase.getReference("user/"+mAuth.getUid()+"/type").setValue("driver");
                    mDatabase.getReference("user/"+mAuth.getUid()+"/join").setValue("on");
                    mDatabase.getReference("user/"+mAuth.getUid()+"/phonetoken").setValue(Common.getInstance().getUserPhone_token());
//                    mDatabase.getReference("user/"+mAuth.getUid()+"/phonenumber").setValue(phone_token);
                    Intent intent = new Intent(PassengerReview.this, OffersActivity.class);
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
                                        Intent intent = new Intent(PassengerReview.this, VerifyActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else if(id_request == 1) {
                                        onShowAlert(getResources().getString(R.string.review_verify), false);
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

    private void onShowAlert(String str, boolean ret) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(str)
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.string_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(ret) {
                            Intent intent = new Intent(PassengerReview.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
        // Create the AlertDialog object and return it
        builder.create();
        builder.show();
    }
}