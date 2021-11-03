package com.mobiledevteam.kuiki;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
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

import java.io.ByteArrayOutputStream;


public class DriverProfileActivity extends AppCompatActivity {

    EditText user_firstname;
    EditText user_lastname;
    EditText user_phonenumber;
    ImageView user_profile;
    Button savechange;

    ImageView ivHeaderPhoto;
    TextView ivHeadName;
    TextView ivHeadBrand;
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

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;

    private String avatar_path = "";
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_profile);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        onGetUserData();

        user_firstname = (EditText) findViewById(R.id.verify_firstname);
        user_lastname = (EditText) findViewById(R.id.verify_lastname);
        user_phonenumber = (EditText) findViewById(R.id.verify_phonenumber);
        savechange = (Button) findViewById(R.id.btn_savechange);
        user_profile = (ImageView) findViewById(R.id.user_profile);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_driver_layout);
        nvDrawer = (NavigationView) findViewById(R.id.driverView);
        View headerLayout = nvDrawer.inflateHeaderView(R.layout.nav_header_driver);
        ivHeaderPhoto = headerLayout.findViewById(R.id.avatar);
        ivHeadName = headerLayout.findViewById(R.id.user_name);
        ivHeadBrand = headerLayout.findViewById(R.id.driver_car_brand);
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
        setSwitch();

        savechange.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onUpdateUserData();
            }
        });
        user_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPickPrifile();
            }
        });
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

    public void onPickPrifile() {
        ImagePicker.Companion.with(this).cameraOnly().crop().maxResultSize(200, 200).start(101);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 101){
            Uri fileUri = data.getData();
            user_profile.setImageURI(fileUri);
            avatar_path = ImagePicker.Companion.getFilePath(data);
        }
    }

    public void onGetUserData() {
        JsonObject json = new JsonObject();
        json.addProperty("uid", mAuth.getUid());
        try {
            Ion.with(this)
                    .load(Common.getInstance().getBaseURL()+"api/getUserData")
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (result != null) {
                                String res = result.get("status").getAsString();
                                if (res.equals("ok")) {
                                    String firstname = result.get("first_name").getAsString();
                                    String lastname = result.get("last_name").getAsString();
                                    String phone = result.get("mobile").getAsString();
                                    String email = result.get("email").getAsString();
                                    String avatar = result.get("avatar").getAsString();

                                    user_firstname.setText(firstname);
                                    user_lastname.setText(lastname);
                                    user_phonenumber.setText(phone);
                                    if(avatar.equals("no_avatar")) {
                                        onGetUserProfile(avatar);
                                    }
                                } else {
                                }
                            } else {
                            }
                        }
                    });
        }catch(Exception e){
            Log.d("Profile Error::", e.toString());
        }
    }

    private void onGetUserProfile(String url) {
        try {
            Picasso.get().load(Common.getInstance().getBaseURL() + "backend/" + url).into(user_profile);
        }catch(Exception e){
            Log.d("Profile Error::", e.toString());
        }
    }

    public void onUpdateUserData() {
        String firstname = user_firstname.getText().toString();
        if(firstname.length() == 0) {
            Toast.makeText(getBaseContext(), getResources().getString(R.string.string_first_name), Toast.LENGTH_LONG).show();
            return;
        }
        String lastname = user_lastname.getText().toString();
        if(lastname.length() == 0) {
            Toast.makeText(getBaseContext(), getResources().getString(R.string.string_last_name), Toast.LENGTH_LONG).show();
            return;
        }

        JsonObject json = new JsonObject();
        json.addProperty("uid", mAuth.getUid());
        json.addProperty("firstname", firstname);
        json.addProperty("lastname", lastname);

        if(avatar_path != "") {
            Bitmap bm = BitmapFactory.decodeFile(avatar_path);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
            byte[] b = baos.toByteArray();
            String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

            json.addProperty("avatar",encodedImage);
        }

        try {
            Ion.with(this)
                    .load(Common.getInstance().getBaseURL()+"api/updateUserData")
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (result != null) {
                                String res = result.get("status").getAsString();
                                if (res.equals("ok")) {
                                    mDatabase.getReference("user/"+mAuth.getUid()+"/avatar").setValue(result.get("avatar").getAsString());
                                    onShowAlert(getResources().getString(R.string.profile_update_ok));
                                } else {
                                }
                            } else {
                            }
                        }
                    });
        }catch(Exception e){
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DriverProfileActivity.this, OffersActivity.class);
        startActivity(intent);
        finish();
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
        btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent=new Intent(getApplicationContext(), DriverProfileActivity.class);
//                startActivity(intent);
//                finish();
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
                    Intent intent = new Intent(DriverProfileActivity.this, HomeActivity.class);
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
                                        Intent intent = new Intent(DriverProfileActivity.this, VerifyActivity.class);
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