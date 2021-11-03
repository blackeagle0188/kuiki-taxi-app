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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

public class UserSettingActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    DatabaseReference mRef;

    private Button _btn_spanish;
    private Button _btn_english;
    private Button _btn_logout;

    private ImageView ivHeaderPhoto;
    private TextView ivHeadName;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        _btn_spanish = (Button)findViewById(R.id.btn_spanish);
        _btn_english = (Button)findViewById(R.id.btn_english);
        _btn_logout = (Button)findViewById(R.id.btn_logout);

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
    private void logOut(){
        mAuth.signOut();
        Intent intent = new Intent(UserSettingActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
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
                Intent intent=new Intent(UserSettingActivity.this, UserProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btn_user_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(UserSettingActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btn_user_ride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(UserSettingActivity.this, TripHistoryActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btn_user_privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(UserSettingActivity.this, ProtectionActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btn_user_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent=new Intent(UserSettingActivity.this, UserSettingActivity.class);
//                startActivity(intent);
//                finish();
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
                    Intent intent = new Intent(UserSettingActivity.this, OffersActivity.class);
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
                                        Intent intent = new Intent(UserSettingActivity.this, VerifyActivity.class);
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
}