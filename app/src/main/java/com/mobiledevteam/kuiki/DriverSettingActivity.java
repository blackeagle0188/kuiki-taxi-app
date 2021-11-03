package com.mobiledevteam.kuiki;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.mobiledevteam.kuiki.Adapter.Offers;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

public class DriverSettingActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    DatabaseReference mRef;

    private Button _btn_spanish;
    private Button _btn_english;
    private Button _btn_logout;
    private SwitchButton switchButton;

    private ImageView ivHeaderPhoto;
    private TextView ivHeadName;
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
    private Button btn_mode;
    private Button btn_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_setting);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        _btn_spanish = (Button)findViewById(R.id.btn_driver_spanish);
        _btn_english = (Button)findViewById(R.id.btn_driver_english);
        _btn_logout = (Button)findViewById(R.id.btn_logout);
        switchButton = (SwitchButton) findViewById(R.id.switch_button);

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

        onGetUserInfo();
        onSetMenuEvent();

        _btn_spanish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSetSpanish();
            }
        });
        _btn_english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSetEnglish();
            }
        });
        _btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });
        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawer.openDrawer(nvDrawer);
            }
        });
    }

    private void onSetSpanish(){
        final Configuration configuration = getResources().getConfiguration();
        LocaleHelper.setLocale(getBaseContext(), "es");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLayoutDirection(new Locale("es"));
        }
        writeFile("es");
        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }
    private void onSetEnglish() {
        final Configuration configuration = getResources().getConfiguration();
        LocaleHelper.setLocale(getBaseContext(), "en");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLayoutDirection(new Locale("en"));
        }
        writeFile("en");
        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

    private  void writeFile(String lang){
        try {
            FileOutputStream fileOutputStream = openFileOutput("lang.pdm", MODE_PRIVATE);
            fileOutputStream.write(lang.getBytes());
            fileOutputStream.close();
        }catch (FileNotFoundException e){
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void logOut(){
        mAuth.signOut();
        Intent intent = new Intent(DriverSettingActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
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
        Intent intent=new Intent(this, OffersActivity.class);
        startActivity(intent);
        finish();
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
//                Intent intent=new Intent(getApplicationContext(), DriverSettingActivity.class);
//                startActivity(intent);
//                finish();
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
            Toast.makeText(DriverSettingActivity.this, getResources().getString(R.string.string_offline), Toast.LENGTH_LONG ).show();
        }else{
            mDatabase.getReference("user/"+mAuth.getUid()+"/type").setValue("user");
            mDatabase.getReference("user/"+mAuth.getUid()+"/join").setValue("off");
            Intent intent=new Intent(getBaseContext(), HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }
}