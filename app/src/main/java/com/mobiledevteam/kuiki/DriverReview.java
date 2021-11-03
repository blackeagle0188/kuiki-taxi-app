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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;

import java.util.Arrays;

public class DriverReview extends AppCompatActivity {

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
    LinearLayout btn_profile;
    LinearLayout btn_offers;
    LinearLayout btn_setting;
    LinearLayout btn_safety;
    LinearLayout btn_help;
    LinearLayout btn_support;
    Button btn_mode;
    Button btn_menu;
    private SwitchButton switchButton;

    private String str_uid;
    private String url_profile;
    private int[] options = new int[4];

    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_review);

        img_profile = (ImageView) findViewById(R.id.review_user_profile);
        txt_name = (TextView) findViewById(R.id.review_user_name);
        rbar_star = (RatingBar) findViewById(R.id.star_driver);
        chk_option1 = (CheckBox) findViewById(R.id.driver_option1);
        chk_option2 = (CheckBox) findViewById(R.id.driver_option2);
        chk_option3 = (CheckBox) findViewById(R.id.driver_option3);
        chk_option4 = (CheckBox) findViewById(R.id.driver_option4);
        txt_comment = (EditText) findViewById(R.id.txt_driver_comment);
        btn_submit = (Button) findViewById(R.id.btn_driver_submit);
        btn_cancel = (Button) findViewById(R.id.txt_driver_cancel);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

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
        btn_menu = (Button) findViewById(R.id.btn_menu);

        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawer.openDrawer(nvDrawer);
            }
        });

        onGetUserInfo();
        onSetMenuEvent();
        setSwitch();

        str_uid = Common.getInstance().getUser_uid();

        onGetDriverInfo();
        onSetButtonEvent();
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

    private void onSetButtonEvent() {
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                Intent intent = new Intent(DriverReview.this, OffersActivity.class);
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
                                onShowAlert();
                            } else {
                            }
                        } else {
                        }
                    }
                });
    }

    private void onShowAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.string_review_submit))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.string_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(DriverReview.this, OffersActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
        // Create the AlertDialog object and return it
        builder.create();
        builder.show();
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
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
            Toast.makeText(DriverReview.this, getResources().getString(R.string.string_offline), Toast.LENGTH_LONG ).show();
        }else{
            mDatabase.getReference("user/"+mAuth.getUid()+"/type").setValue("user");
            mDatabase.getReference("user/"+mAuth.getUid()+"/join").setValue("off");
            Intent intent=new Intent(getBaseContext(), HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }
}