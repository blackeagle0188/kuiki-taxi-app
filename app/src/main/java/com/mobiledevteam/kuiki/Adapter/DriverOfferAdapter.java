package com.mobiledevteam.kuiki.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.mobiledevteam.kuiki.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DriverOfferAdapter extends ArrayAdapter<DriverOffer> {
    private Context mContext;
    private List<DriverOffer> allEventList = new ArrayList<>();

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    public DriverOfferAdapter(Context context, ArrayList<DriverOffer> list) {
        super(context, 0 , list);
        mContext = context;
        allEventList = list;
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
    }

    public void clearData(){
        allEventList.clear();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.driver_offer,parent,false);

        DriverOffer offer = allEventList.get(position);
        Button btn_adept_driver = listItem.findViewById(R.id.btn_adept_driver);

        offer.accept(btn_adept_driver, mDatabase, mAuth);
        Button btn_reject_driver = listItem.findViewById(R.id.btn_reject_driver);
        offer.reject(btn_reject_driver, mDatabase);
        ProgressBar progressBar = (ProgressBar) listItem.findViewById(R.id.driver_offer_progress);
        offer.setProgressValue(100, progressBar);

        ImageView clinic_image = (ImageView) listItem.findViewById(R.id.driver_img_avatar);
        ImageView img_star = (ImageView) listItem.findViewById(R.id.img_star);
        img_star.setImageResource(R.drawable.ic_baseline_star_24);
        Picasso.get().load(offer.getmImage()).into(clinic_image);
        Log.d("avatar_url:", offer.getmImage());
        TextView brand = (TextView) listItem.findViewById(R.id.txt_driver_brand);
        brand.setText(offer.getBrand());
        TextView name = (TextView) listItem.findViewById(R.id.txt_driver_name);
        name.setText(offer.getName());
        TextView price = (TextView) listItem.findViewById(R.id.txt_driver_price);
        price.setText(offer.getPrice());
        TextView time = (TextView) listItem.findViewById(R.id.txt_driver_time);
        time.setText(offer.getTime());
        TextView distance = (TextView) listItem.findViewById(R.id.txt_driver_distance);
        distance.setText(offer.getDistance());
        TextView review = (TextView) listItem.findViewById(R.id.txt_driver_offer);
        review.setText(offer.getReview());
        return listItem;
    }
}