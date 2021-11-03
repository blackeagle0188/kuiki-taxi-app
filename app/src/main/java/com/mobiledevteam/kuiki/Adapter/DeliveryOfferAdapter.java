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

public class DeliveryOfferAdapter extends ArrayAdapter<DeliveryOffer> {
    private Context mContext;
    private List<DeliveryOffer> allEventList = new ArrayList<>();

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    public DeliveryOfferAdapter(Context context, ArrayList<DeliveryOffer> list) {
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
            listItem = LayoutInflater.from(mContext).inflate(R.layout.delivery_offer,parent,false);

        DeliveryOffer offer = allEventList.get(position);
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
        TextView name = (TextView) listItem.findViewById(R.id.txt_driver_name);
        name.setText(offer.getName());
        TextView price = (TextView) listItem.findViewById(R.id.txt_driver_price);
        price.setText(offer.getPrice() + "MXN");
        TextView time = (TextView) listItem.findViewById(R.id.txt_driver_time);
        time.setText(offer.getTime() + "MIN");
        TextView review = (TextView) listItem.findViewById(R.id.txt_driver_review);
        review.setText(offer.getReview());
        return listItem;
    }
}
