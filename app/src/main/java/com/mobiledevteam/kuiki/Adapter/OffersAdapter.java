package com.mobiledevteam.kuiki.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobiledevteam.kuiki.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class OffersAdapter extends ArrayAdapter<Offers> {
    private Context mContext;
    private List<Offers> allEventList = new ArrayList<>();

    public OffersAdapter(Context context, ArrayList<Offers> list) {
        super(context, 0 , list);
        mContext = context;
        allEventList = list;
    }

    public void clearData(){
        allEventList.clear();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.item_offfer,parent,false);

        Offers offer = allEventList.get(position);

        ImageView clinic_image = (ImageView) listItem.findViewById(R.id.driver_img_avatar);
        ImageView img_star = (ImageView) listItem.findViewById(R.id.img_star);
        img_star.setImageResource(R.drawable.ic_baseline_star_24);
        Picasso.get().load(offer.getmImage()).into(clinic_image);
        Log.d("avatar_url:", offer.getmImage());
        TextView name = (TextView) listItem.findViewById(R.id.txt_driver_name);
        name.setText(offer.getName());
        TextView startLocation = (TextView) listItem.findViewById(R.id.txt_start);
        startLocation.setText(offer.getStartLocation());
        TextView endlocation = (TextView) listItem.findViewById(R.id.txt_end);
        endlocation.setText(offer.getEndLocation());
        TextView price = (TextView) listItem.findViewById(R.id.txt_offer_price);
        price.setText(offer.getPrice());
        TextView distance = (TextView) listItem.findViewById(R.id.txt_offer_distance);
        distance.setText(offer.getDistance());
        TextView comment = (TextView) listItem.findViewById(R.id.txt_comment);
        comment.setText(offer.getComment());
        TextView review = (TextView) listItem.findViewById(R.id.txt_user_review);
        review.setText(offer.getReview());
        return listItem;
    }
}