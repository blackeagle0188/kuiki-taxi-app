package com.mobiledevteam.kuiki;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import org.checkerframework.checker.linear.qual.Linear;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class VerifyActivity extends AppCompatActivity {

    ImageView front_id;
    ImageView back_id;
    ImageView address_bill;
    EditText curp;
    ImageView upload_profile;
    EditText user_firstname;
    EditText user_lastname;
    EditText gender;
    Spinner color;
    EditText user_phone;
    Button btn_submit;
    Spinner spn_brand;
    Spinner spn_subbrand;
    EditText year;
    LinearLayout layout_subbrand;
    ImageView car_image;
    EditText txt_plate;
    ImageView car_permit;
    ImageView insur_cert;
    LinearLayout option_car;
    LinearLayout option_motocycle;

    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    DatabaseReference mRef;

    private ProgressDialog progressDialog;
    private int type;
    private String front_path;
    private String back_path;
    private String str_gender;
    private String profile_path;
    private String car_path;
    private String curp_path;
    private String address_path;
    private String carpermit_path;
    private String insur_path;
    private int color_number = 0;
    private String car_year;

    ImageView ivHeaderPhoto;
    TextView ivHeadName;
    private Toolbar toolbar;
    private DrawerLayout mDrawer;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;
    private LinearLayout btn_go_profile;
    private LinearLayout btn_user_home;
    private LinearLayout btn_user_ride;
    private LinearLayout btn_user_setting;
    private LinearLayout btn_user_help;
    private LinearLayout btn_user_support;
    private Button btn_mode;
    private Button btn_menu;

    List<JsonObject> brands = new ArrayList<JsonObject>();
    List<JsonObject> sub_brands = new ArrayList<JsonObject>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        front_id = (ImageView) findViewById(R.id.front_id);
        back_id = (ImageView) findViewById(R.id.back_id);
        address_bill = (ImageView) findViewById(R.id.img_address_bill);
        curp = (EditText) findViewById(R.id.img_curp);
        upload_profile = (ImageView) findViewById(R.id.signup_avatar);
        user_firstname= (EditText) findViewById(R.id.verify_firstname);
        user_lastname = (EditText) findViewById(R.id.verify_lastname);
        gender = (EditText) findViewById(R.id.verify_gender);
        user_phone = (EditText) findViewById(R.id.verify_phonenumber);
        btn_submit = (Button) findViewById(R.id.btn_submit_id);
        color = (Spinner) findViewById(R.id.spn_color);
        spn_brand = (Spinner) findViewById(R.id.spn_brand);
        spn_subbrand = (Spinner) findViewById(R.id.spn_subbrand);
        year = (EditText) findViewById(R.id.car_year);
        layout_subbrand = (LinearLayout) findViewById(R.id.layout_subbrand);
        car_image = (ImageView) findViewById(R.id.car_image);
        txt_plate = (EditText) findViewById(R.id.txt_plate);
        car_permit = (ImageView) findViewById(R.id.car_permit);
        insur_cert = (ImageView) findViewById(R.id.img_insurance);
        option_car = (LinearLayout) findViewById(R.id.btn_trip_option);
        option_motocycle = (LinearLayout) findViewById(R.id.btn_delivery_option);

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
        btn_user_setting = (LinearLayout) headerLayout.findViewById(R.id.btn_user_setting);
        btn_user_help = (LinearLayout) headerLayout.findViewById(R.id.btn_user_help);
        btn_user_support = (LinearLayout) headerLayout.findViewById(R.id.btn_user_support);
        btn_mode = (Button) findViewById(R.id.btn_passenger_mode);
        btn_menu = (Button) findViewById(R.id.btn_menu);

        progressDialog = new ProgressDialog(this, R.style.AppTheme_Bright_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getResources().getString(R.string.string_submitting));
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);

        type = 0;
        front_path = "";
        back_path = "";
        profile_path = "";
        car_path = "";
        address_path = "";
        curp_path = "";
        carpermit_path = "";
        insur_path = "";
        car_year = "";

        Picasso.get().load(Common.getInstance().getBaseURL() + "backend/" + Common.getInstance().getAvatar()).into(upload_profile);

        onGetUserData();
        onGetUserInfo();
        onSetMenuEvent();

        onGetBrands();
        onSetColor();

        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawer.openDrawer(nvDrawer);
            }
        });

        option_car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = 0;
                option_car.setBackground(getResources().getDrawable(R.drawable.blue_border));
                option_motocycle.setBackground(getResources().getDrawable(R.drawable.grey_border));
            }
        });

        option_motocycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = 1;
                option_car.setBackground(getResources().getDrawable(R.drawable.grey_border));
                option_motocycle.setBackground(getResources().getDrawable(R.drawable.blue_border));
                layout_subbrand.setVisibility(View.GONE);
            }
        });

        front_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPickIdFront();
            }
        });
        back_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPickIdBack();
            }
        });
        address_bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPickAddress();
            }
        });
        car_permit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPickCarPermit();
            }
        });
        insur_cert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPickInsurCert();
            }
        });
        upload_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickProfile();
            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitID();
            }
        });
        spn_brand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                TextView brand_name = (TextView) view;
                String str_brand = brand_name.getText().toString();
                List<String> list = new ArrayList<String>();
                int index = 0;
                for(int j = 0; j < brands.size(); j++) {
                    JsonObject jsonObject = brands.get(j);
                    String brand = jsonObject.get("brand").getAsString();
                    int id = jsonObject.get("id").getAsInt();
                    int owner = Integer.parseInt(jsonObject.get("owner").getAsString());
                    if(str_brand == brand) {
                        index = id;
                        break;
                    }
                }
                for(int j = 0; j < brands.size(); j++) {
                    JsonObject jsonObject = brands.get(j);
                    String brand = jsonObject.get("brand").getAsString();
                    int owner = Integer.parseInt(jsonObject.get("owner").getAsString());
                    if(owner == index) {
                        list.add(brand);
//                        index = j;
                    }
                }
                if(type == 0) {
                    onSetSubBrand(list);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        color.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                color_number = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        car_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPickCar();
            }
        });
        year.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNormalPicker();
            }
        });

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK){
            Uri fileUri;
            if(requestCode == 101) {
                fileUri = data.getData();
                front_id.setImageURI(fileUri);
                front_path = ImagePicker.Companion.getFilePath(data);
            } else if(requestCode == 102) {
                fileUri = data.getData();
                back_id.setImageURI(fileUri);
                back_path = ImagePicker.Companion.getFilePath(data);
            } else if(requestCode == 103) {
                fileUri = data.getData();
                upload_profile.setImageURI(fileUri);
                profile_path = ImagePicker.Companion.getFilePath(data);
            } else if(requestCode == 104) {
                fileUri = data.getData();
                car_image.setImageURI(fileUri);
                car_path = ImagePicker.Companion.getFilePath(data);
            } else if(requestCode == 106) {
                fileUri = data.getData();
                address_bill.setImageURI(fileUri);
                address_path = ImagePicker.Companion.getFilePath(data);
            } else if(requestCode == 108) {
                fileUri = data.getData();
                car_permit.setImageURI(fileUri);
                carpermit_path = ImagePicker.Companion.getFilePath(data);
            } else if(requestCode == 109) {
                fileUri = data.getData();
                insur_cert.setImageURI(fileUri);
                insur_path = ImagePicker.Companion.getFilePath(data);
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(VerifyActivity.this, HomeActivity.class);//LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void onPickIdFront() {
        ImagePicker.Companion.with(this).cameraOnly().crop().maxResultSize(500, 500).start(101);
    }

    public void onPickIdBack() {
        ImagePicker.Companion.with(this).cameraOnly().crop().maxResultSize(500, 500).start(102);
    }

    public void onPickProfile() {
        ImagePicker.Companion.with(this).cameraOnly().crop().maxResultSize(200, 200).start(103);
    }

    public void onPickCar() {
        ImagePicker.Companion.with(this).cameraOnly().crop().maxResultSize(500, 500).start(104);
    }

    public void onPickBirth() {
        ImagePicker.Companion.with(this).cameraOnly().crop().maxResultSize(500, 500).start(105);
    }

    public void onPickAddress() {
        ImagePicker.Companion.with(this).cameraOnly().crop().maxResultSize(500, 500).start(106);
    }

    public void onPickCarPermit() {
        ImagePicker.Companion.with(this).cameraOnly().crop().maxResultSize(500, 500).start(108);
    }

    public void onPickInsurCert() {
        ImagePicker.Companion.with(this).cameraOnly().crop().maxResultSize(500, 500).start(109);
    }

    public void submitID() {
        if(front_path.equals("")) {
            onShowAlert(getResources().getString(R.string.string_id_front), false);
            return;
        }

        if(back_path.equals("")) {
            onShowAlert(getResources().getString(R.string.string_id_back), false);
            return;
        }

        if(address_path.equals("")) {
            onShowAlert(getResources().getString(R.string.string_no_addressimg), false);
            return;
        }

        String curp_path = curp.getText().toString();
        if(curp_path.equals("")) {
            onShowAlert(getResources().getString(R.string.string_no_curp), false);
            return;
        }

        String firstname = user_firstname.getText().toString();
        if(firstname.equals("")) {
            onShowAlert(getResources().getString(R.string.string_first_name), false);
            return;
        }
        String lastname = user_lastname.getText().toString();
        if(lastname.length() == 0) {
            onShowAlert(getResources().getString(R.string.string_last_name), false);
            return;
        }

        str_gender = gender.getText().toString();
        if(str_gender.equals("")) {
            onShowAlert(getResources().getString(R.string.string_no_gender), false);
            return;
        }

        String selectedBrand = spn_brand.getSelectedItem().toString();

        if(selectedBrand.equals("")) {
            onShowAlert(getResources().getString(R.string.string_brand), false);
            return;
        }

        String selectedSubBrand = "";

        if(layout_subbrand.getVisibility() == View.VISIBLE && spn_subbrand.getAdapter().getCount() > 0) {
            selectedSubBrand = spn_subbrand.getSelectedItem().toString();
        }

        if(car_year.equals("")) {
            onShowAlert(getResources().getString(R.string.string_no_caryear), false);
            return;
        }

        String plate = txt_plate.getText().toString();
        if(plate.equals("")) {
            onShowAlert(getResources().getString(R.string.string_car_plate), false);
            return;
        }

        if(carpermit_path.equals("")) {
            onShowAlert(getResources().getString(R.string.string_no_carpermit), false);
            return;
        }

        if(insur_path.equals("")) {
            onShowAlert(getResources().getString(R.string.string_no_insurcert), false);
            return;
        }

        if(car_path.equals("")) {
            onShowAlert(getResources().getString(R.string.string_no_catimg), false);
            return;
        }

        JsonObject json = new JsonObject();
        json.addProperty("uid", mAuth.getUid());
        json.addProperty("type", type);
        json.addProperty("firstname", firstname);
        json.addProperty("lastname", lastname);

        Bitmap bm = BitmapFactory.decodeFile(front_path);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();
        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

        json.addProperty("front_photo",encodedImage);

        bm = BitmapFactory.decodeFile(back_path);
        baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
        b = baos.toByteArray();
        encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

        json.addProperty("back_photo",encodedImage);

        if(!profile_path.equals("")) {
            bm = BitmapFactory.decodeFile(profile_path);
            baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
            b = baos.toByteArray();
            encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

            json.addProperty("profile_photo", encodedImage);
        }

        json.addProperty("brand",selectedBrand);

        if(!selectedSubBrand.equals("")) {
            json.addProperty("sub_brand",selectedSubBrand);
        }

        bm = BitmapFactory.decodeFile(address_path);
        baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
        b = baos.toByteArray();
        encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

        json.addProperty("address_image", encodedImage);

        json.addProperty("curp", curp_path);

        bm = BitmapFactory.decodeFile(car_path);
        baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
        b = baos.toByteArray();
        encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

        json.addProperty("car_image", encodedImage);

        bm = BitmapFactory.decodeFile(carpermit_path);
        baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
        b = baos.toByteArray();
        encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

        json.addProperty("carpermit_image", encodedImage);

        bm = BitmapFactory.decodeFile(insur_path);
        baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
        b = baos.toByteArray();
        encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

        json.addProperty("insur_image", encodedImage);



        json.addProperty("plate",plate);
        json.addProperty("gender",str_gender);
        json.addProperty("year", car_year);
        json.addProperty("car_color", color_number);

        mDatabase.getReference("user/" + mAuth.getUid() + "/brand").setValue(selectedBrand + "  " + selectedSubBrand);
        mDatabase.getReference("user/" + mAuth.getUid() + "/plate").setValue(plate);

        progressDialog.show();
        try {
            Ion.with(this)
                    .load(Common.getInstance().getBaseURL()+"api/verify")
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (result != null) {
                                String res = result.get("status").getAsString();
                                if (res.equals("ok")) {
                                    progressDialog.dismiss();
                                    onShowAlert(getResources().getString(R.string.submit_verify), true);
                                } else {
                                    progressDialog.dismiss();
                                    onShowAlert(getResources().getString(R.string.failed_verify_submit), false);
                                }
                            } else {
                                progressDialog.dismiss();
                                onShowAlert(getResources().getString(R.string.failed_verify_submit), false);
                            }

                        }
                    });
        }catch(Exception e){
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

                                    user_firstname.setText(firstname);
                                    user_lastname.setText(lastname);
                                    user_phone.setText(phone);
                                } else {
                                }
                            } else {
                            }
                        }
                    });
        }catch(Exception e){
        }
    }

    private void onSetColor() {
        List<String> list = new ArrayList<String>();
        list.add(getResources().getString(R.string.string_color_red));
        list.add(getResources().getString(R.string.string_color_blue));
        list.add(getResources().getString(R.string.string_color_black));
        list.add(getResources().getString(R.string.string_color_gray));
        list.add(getResources().getString(R.string.string_color_light_gray));
        list.add(getResources().getString(R.string.string_color_dark_gray));
        list.add(getResources().getString(R.string.string_color_beige));
        list.add(getResources().getString(R.string.string_color_brown));
        list.add(getResources().getString(R.string.string_color_green));
        list.add(getResources().getString(R.string.string_color_purple));
        list.add(getResources().getString(R.string.string_color_violet));
        list.add(getResources().getString(R.string.string_color_white));
        list.add(getResources().getString(R.string.string_color_yellow));
        list.add(getResources().getString(R.string.string_color_orange));

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        color.setAdapter(dataAdapter);
    }

    private void onGetBrands() {
        JsonObject json = new JsonObject();

        try {
            Ion.with(this)
                    .load(Common.getInstance().getBaseURL()+"api/getBrands")
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (result != null) {
                                String res = result.get("status").getAsString();
                                if (res.equals("ok")) {
                                    JsonArray json = result.get("brands").getAsJsonArray();
                                    for(JsonElement element: json) {
                                        JsonObject jsonObject = element.getAsJsonObject();
                                        brands.add(jsonObject);
                                    }
                                    List<String> list = new ArrayList<String>();
                                    for(int i = 0; i < brands.size(); i++) {
                                        JsonObject jsonObject = brands.get(i);
                                        int id = Integer.parseInt(jsonObject.get("id").getAsString());
                                        String brand = jsonObject.get("brand").getAsString();
                                        int owner = Integer.parseInt(jsonObject.get("owner").getAsString());
                                        if(owner == 0) {
                                            list.add(brand);
                                        }
                                    }
                                    onSetBrand(list);
                                } else {
                                }
                            } else {
                            }
                        }
                    });
        }catch(Exception e){
        }
    }

    private void onSetBrand(List<String> list) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_brand.setAdapter(dataAdapter);
    }

    private void onSetSubBrand(List<String> list) {
        if(list != null && !list.isEmpty()) {
            layout_subbrand.setVisibility(View.VISIBLE);
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);

            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spn_subbrand.setAdapter(dataAdapter);
        } else {
            layout_subbrand.setVisibility(View.GONE);
        }
    }

    private void onShowAlert(String msg, boolean ret) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg)
                .setPositiveButton(getResources().getString(R.string.string_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(ret) {
                            Intent intent=new Intent(VerifyActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }
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
                Intent intent=new Intent(VerifyActivity.this, UserProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btn_user_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(VerifyActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btn_user_ride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(VerifyActivity.this, TripHistoryActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btn_user_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(VerifyActivity.this, UserSettingActivity.class);
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
                    Intent intent = new Intent(VerifyActivity.this, OffersActivity.class);
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
                                        Intent intent = new Intent(VerifyActivity.this, VerifyActivity.class);
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

    private void setNormalPicker() {
        final Calendar today = Calendar.getInstance();
                MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(VerifyActivity.this, new MonthPickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(int selectedMonth, int selectedYear) {
                    }
                }, today.get(Calendar.YEAR), today.get(Calendar.MONTH));

                builder.setActivatedMonth(Calendar.JULY)
                        .setMinYear(1990)
                        .setActivatedYear(2021)
                        .setMaxYear(2100)
//                        .setMinMonth(Calendar.FEBRUARY)
                        .setTitle("Select trading month")
//                        .setMonthRange(Calendar.FEBRUARY, Calendar.NOVEMBER)
                        // .setMaxMonth(Calendar.OCTOBER)
                        // .setYearRange(1890, 1890)
                        // .setMonthAndYearRange(Calendar.FEBRUARY, Calendar.OCTOBER, 1890, 1890)
                        //.showMonthOnly()
                         .showYearOnly()
                        .setOnMonthChangedListener(new MonthPickerDialog.OnMonthChangedListener() {
                            @Override
                            public void onMonthChanged(int selectedMonth) {
                                // Toast.makeText(MainActivity.this, " Selected month : " + selectedMonth, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setOnYearChangedListener(new MonthPickerDialog.OnYearChangedListener() {
                            @Override
                            public void onYearChanged(int selectedYear) {
                                // Toast.makeText(MainActivity.this, " Selected year : " + selectedYear, Toast.LENGTH_SHORT).show();
                                car_year = String.valueOf(selectedYear);
                                year.setText(car_year);
                            }
                        })
                        .build()
                        .show();
    }
}