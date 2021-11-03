package com.mobiledevteam.kuiki;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity {

    String androidDeviceId = "";
    private int SPLASH_TIME = 1000;
    private boolean firstFlag;
    private boolean secondFlag;
    private static final int REQUEST_LOCATION = 111;
    private String sel_lang = "no";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        readFile();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]
                    {ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }else{
            Handler handler=new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    moveToLogin();
                }
            },1000);
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
//        Log.d("grantResultsLength", Integer.toString(requestCode));
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Handler handler=new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    moveToLogin();
                }
            },1000);
        }else{
            new AlertDialog.Builder(MainActivity.this)
                    .setMessage(getResources().getString(R.string.permission_deny))
                    .setCancelable(false)
                    .setPositiveButton("ok", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", getPackageName(), null));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finishAffinity();
                            System.exit(0);
                        }
                    }).show();
        }
//        Toast.makeText(this, "test", Toast.LENGTH_SHORT).show();
    }
    private void moveToLogin(){
        if(sel_lang.equals("en")){
            final Configuration configuration = getResources().getConfiguration();
            LocaleHelper.setLocale(getBaseContext(), "en");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                configuration.setLayoutDirection(new Locale("en"));
            }
            getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
            Intent intent=new Intent(getBaseContext(), CheckUser.class);
            startActivity(intent);
            finish();

        }else if(sel_lang.equals("es")){
            final Configuration configuration = getResources().getConfiguration();
            LocaleHelper.setLocale(getBaseContext(), "es");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                configuration.setLayoutDirection(new Locale("es"));
            }
            getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
            Intent intent=new Intent(getBaseContext(), CheckUser.class);
            startActivity(intent);
            finish();
        }else{
            final Configuration configuration = getResources().getConfiguration();
            LocaleHelper.setLocale(getBaseContext(), "es");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                configuration.setLayoutDirection(new Locale("es"));
            }
            getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
            Intent intent = new Intent(MainActivity.this, CheckUser.class);
            overridePendingTransition(0, 0);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        }
    }

    private void readFile(){
        try {
            FileInputStream fileInputStream = openFileInput("lang.pdm");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer stringBuffer = new StringBuffer();
            String lines;
            while((lines = bufferedReader.readLine()) != null){
                sel_lang = lines;
            }
        }catch (FileNotFoundException e){
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}