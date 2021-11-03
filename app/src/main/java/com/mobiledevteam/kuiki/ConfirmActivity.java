package com.mobiledevteam.kuiki;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.concurrent.TimeUnit;

public class ConfirmActivity extends AppCompatActivity implements LocationListener {

    EditText _confirmCode;
    private String _phonenumber;
    private String verifiy_id;
    private FirebaseAuth mAuth;
    private TextView _phone_number;
    private TextView _sendCode;
    Button _btn_confirm;
    TextView _wrongCode;
    private ProgressDialog progressDialog;
    private LocationManager locationmanager;
    private PhoneAuthProvider.ForceResendingToken forceResendingToken;
    private PhoneAuthCredential phoneAuthCredential;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        mAuth = FirebaseAuth.getInstance();
        _phonenumber = Common.getInstance().getPhonenumber();
        _confirmCode = (EditText) findViewById(R.id.confirm_code);
        _phone_number = (TextView)findViewById(R.id.phone_number);
        _btn_confirm = (Button)findViewById(R.id.btn_verify_code);
        _wrongCode = (TextView)findViewById(R.id.txt_wrongcode);
        _sendCode = (TextView)findViewById(R.id.send_again);

        _phone_number.setText(_phonenumber);
        progressDialog = new ProgressDialog(this, R.style.AppTheme_Bright_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getResources().getString(R.string.string_authenticating));
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        _sendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resendVerificationCode(_phonenumber);
            }
        });

        sendVerificationCode(_phonenumber);
        ConfirmCode();
    }

    private void sendVerificationCode(String phonenumber){
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phonenumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(
                                this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)
                        // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void resendVerificationCode(String phonenumber){
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phonenumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)
                        .setForceResendingToken(forceResendingToken)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            phoneAuthCredential = credential;
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(ConfirmActivity.this, R.string.failed_verify, Toast.LENGTH_LONG).show();
            Log.d("Failed::", e.getMessage());
        }

        @Override
        public void onCodeSent(@NonNull String verificationId,
                @NonNull PhoneAuthProvider.ForceResendingToken token) {
            verifiy_id = verificationId;
            forceResendingToken = token;
        }
    };

    private void verifyCode(String code){
        try {
            if(code.equals("")) {
                return;
            }
            progressDialog.show();
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verifiy_id, code);
            signInWithCredential(credential);
        } catch (Exception e) {

        }
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            _wrongCode.setVisibility(View.GONE);
                            FirebaseUser user = task.getResult().getUser();
                            Log.d("user uid:", user.getUid());
                            Common.getInstance().setUid(user.getUid());
                            checkUserExist(user.getUid());
                        }else{
                            progressDialog.dismiss();
                            _wrongCode.setVisibility(View.VISIBLE);
                            _confirmCode.getText().clear();
//                            Toast.makeText(ConfirmActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            Log.d("Error:", task.getException().getMessage());
                        }
                    }
                });
    }

    private void ConfirmCode(){
        _btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyCode(_confirmCode.getText().toString());
            }
        });
    }

    private void checkUserExist( String uid ) {
        JsonObject json = new JsonObject();
        json.addProperty("uid", uid);

        try {
            Ion.with(this)
                    .load(Common.getInstance().getBaseURL()+"api/check_user_exist")
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            progressDialog.dismiss();
                            if (result != null) {
                                progressDialog.dismiss();
                                String res = result.get("status").getAsString();
                                Log.d("result::", res);
                                if (res.equals("ok")) {
                                    Log.d("result::", "HomeActivity");
                                    Intent intent = new Intent(ConfirmActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Log.d("result::", "SignUp");
                                    Intent intent = new Intent(ConfirmActivity.this, SignUpActivity.class);
                                    intent.putExtra("uid", uid);
                                    startActivity(intent);
                                    finish();
                                }
                                finish();
                            } else {
                                progressDialog.dismiss();
                            }
                        }
                    });
        }catch(Exception e){
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Log.d("Location:", String.valueOf( location.getLatitude()));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}