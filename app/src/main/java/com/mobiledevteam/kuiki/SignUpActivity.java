package com.mobiledevteam.kuiki;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.ByteArrayOutputStream;

public class SignUpActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;

    EditText user_firstname;
    EditText user_lastname;
    Button register;
    ImageView user_profile;

    private String _phonenumber;
    private String _uid;

    private ProgressDialog progressDialog;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private String avatar_path = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        _phonenumber = Common.getInstance().getPhonenumber();
        _uid = getIntent().getStringExtra("uid");

        user_firstname = (EditText) findViewById(R.id.verify_firstname);
        user_lastname = (EditText) findViewById(R.id.user_lastname);
        register = (Button) findViewById(R.id.btn_register);
        user_profile = (ImageView) findViewById(R.id.signup_avatar);

        progressDialog = new ProgressDialog(this, R.style.AppTheme_Bright_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getResources().getString(R.string.string_registering));
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRegister();
            }
        });

        user_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPickPrifile();
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

    private void onRegister() {
        JsonObject json = new JsonObject();

        if(avatar_path == "") {
            Toast.makeText(getBaseContext(), getResources().getString(R.string.string_profile_image), Toast.LENGTH_LONG).show();
            return;
        }

        String first_name = user_firstname.getText().toString();
        if(first_name.length() == 0) {
            Toast.makeText(getBaseContext(), getResources().getString(R.string.string_first_name), Toast.LENGTH_LONG).show();
            return;
        }
        String last_name = user_lastname.getText().toString();
        if(last_name.length() == 0) {
            Toast.makeText(getBaseContext(), getResources().getString(R.string.string_last_name), Toast.LENGTH_LONG).show();
            return;
        }

        json.addProperty("uid", _uid);
        json.addProperty("phone", _phonenumber);
        json.addProperty("first_name", first_name);
        json.addProperty("last_name", last_name);

        if(avatar_path != "") {
            Bitmap bm = BitmapFactory.decodeFile(avatar_path);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
            byte[] b = baos.toByteArray();
            String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

            json.addProperty("avatar",encodedImage);
        }

        register.setClickable(false);

        progressDialog.show();
        try {
            Ion.with(this)
                    .load(Common.getInstance().getBaseURL()+"api/register")
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            progressDialog.dismiss();
                            Log.d("HomeActivity::", result.get("status").toString());
                            if (result != null) {
                                progressDialog.dismiss();
                                String res = result.get("status").getAsString();
                                if(res.equals("ok")) {
                                    String avatar = result.get("avatar").getAsString();
                                    onAddUserToDatabase(avatar);
                                } else {

                                }
                            } else {
                                progressDialog.dismiss();
                            }
                        }
                    });
        }catch(Exception e){
        }
    }

    private void onAddUserToDatabase(String avatar){
        mDatabase.getReference("user/"+mAuth.getUid()+"/status").setValue("on");
        mDatabase.getReference("user/"+mAuth.getUid()+"/type").setValue("user");
        mDatabase.getReference("user/"+mAuth.getUid()+"/latitude").setValue(0.0);
        mDatabase.getReference("user/"+mAuth.getUid()+"/longitude").setValue(0.0);
        mDatabase.getReference("user/"+mAuth.getUid()+"/enable").setValue("no");
        mDatabase.getReference("user/"+mAuth.getUid()+"/phonenumber").setValue(_phonenumber);
        mDatabase.getReference("user/"+mAuth.getUid()+"/avatar").setValue(avatar);
        mDatabase.getReference("user/"+mAuth.getUid()+"/username").setValue(user_firstname.getText().toString() + " " + user_lastname.getText().toString());

        Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}