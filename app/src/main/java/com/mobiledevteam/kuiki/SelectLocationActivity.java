package com.mobiledevteam.kuiki;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

import static java.util.Locale.getDefault;

public class SelectLocationActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnCameraIdleListener {

    private ImageView img_user_location;
    private Button txt_location_name;
    private Button btn_map_location;
    private Button btn_back;

    private GoogleMap GMap;
    private LocationManager locationManager;
    private SupportMapFragment mapFragment;
    private Geocoder geocoder;

    private LatLng my_location;

    private List<Address> start_addresses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);

        img_user_location = (ImageView) findViewById(R.id.img_user_location);
        txt_location_name = (Button) findViewById(R.id.txt_location_name);
        btn_map_location = (Button) findViewById(R.id.btn_map_location);
        btn_back = (Button) findViewById(R.id.btn_back);

        geocoder = new Geocoder(this, getDefault());
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        Location LocationGps;
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(new Criteria(), true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        } else {
            LocationGps = locationManager.getLastKnownLocation(provider);
        }
        if (LocationGps != null) {
            my_location = new LatLng(LocationGps.getLatitude(), LocationGps.getLongitude());
        } else {
            my_location = new LatLng(48.85299, 2.34288);
        }
        Bitmap bitmap = Common.getInstance().getBitmap();
        img_user_location.setImageBitmap(bitmap);
        img_user_location.setPadding(0, 0, 0, bitmap.getHeight());

        mapFragment.getMapAsync(SelectLocationActivity.this);

        btn_map_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("lat", my_location.latitude);
                intent.putExtra("lng", my_location.longitude);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        GMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(my_location,14));
        googleMap.setOnCameraIdleListener(this);

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
                Log.d("location::", String.valueOf(latLng));
                my_location = latLng;
                try {
                    start_addresses = geocoder.getFromLocation(my_location.latitude, my_location.longitude, 1);
                    String start_city = start_addresses.get(0).getLocality();
                    String throughfare = start_addresses.get(0).getThoroughfare();
                    txt_location_name.setText(throughfare + ", " + start_city);
                    txt_location_name.setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                GMap.moveCamera(CameraUpdateFactory.newLatLngZoom(my_location, googleMap.getCameraPosition().zoom));
            }
        });
    }

    @Override
    public void onCameraIdle() {
        LatLng location = GMap.getCameraPosition().target;
        Log.d("location::", String.valueOf(location));
        my_location = location;
        try {
            start_addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1);
            String start_city = start_addresses.get(0).getLocality();
            String throughfare = start_addresses.get(0).getThoroughfare();
            txt_location_name.setText(throughfare + ", " + start_city);
            txt_location_name.setVisibility(View.VISIBLE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        GMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, GMap.getCameraPosition().zoom));
    }
}