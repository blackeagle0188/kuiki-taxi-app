package com.mobiledevteam.kuiki;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.hbb20.CountryCodePicker;

public class PhoneVerify extends AppCompatActivity implements LocationListener {

    private FirebaseAuth mAuth;
    Button _confirmBtn;
    EditText _phoneNumber;
    CountryCodePicker _cCodePicker;
    int _valide_number = 0;
    String _phone_Number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verify);

        mAuth = FirebaseAuth.getInstance();

        _confirmBtn = (Button)findViewById(R.id.btn_sendsms);
        _phoneNumber = (EditText) findViewById(R.id.et_phone);
        _cCodePicker = (CountryCodePicker) findViewById(R.id.ccp);
        assignViews();
        _confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmPhone();
            }
        });
    }

    @Override
    public void onBackPressed() {
//        finish();
    }

    public void assignViews(){
        _cCodePicker.registerCarrierNumberEditText(_phoneNumber);
        _cCodePicker.setPhoneNumberValidityChangeListener(new CountryCodePicker.PhoneNumberValidityChangeListener() {
            @Override
            public void onValidityChanged(boolean isValidNumber) {
                if(isValidNumber){
                    _phone_Number = _cCodePicker.getFullNumberWithPlus();
                    _valide_number = 1;
//                    Toast.makeText(PhoneActivity.this, _phone_Number, Toast.LENGTH_SHORT).show();
                }else{
                    _valide_number = 0;
                }
            }
        });
    }

    public void confirmPhone(){
        if(_valide_number == 1){
            Common.getInstance().setPhonenumber(_phone_Number);
            Intent intent = new Intent(getBaseContext(), ConfirmActivity.class);
            startActivity(intent);
            finish();
        }else{
            Toast.makeText(PhoneVerify.this, getResources().getString(R.string.string_phone_invalid), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("LocationPhone: ", String.valueOf(location.getLatitude()));
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