package com.mobiledevteam.kuiki.Adapter;

import android.content.Context;
import android.graphics.Color;
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

public class TripHistoryAdapter extends ArrayAdapter<TripHistory> {
    private Context mContext;
    private List<TripHistory> allEventList = new ArrayList<>();

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    public TripHistoryAdapter(Context context, ArrayList<TripHistory> list) {
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
            listItem = LayoutInflater.from(mContext).inflate(R.layout.trip_history,parent,false);

        TripHistory trip = allEventList.get(position);

        TextView date = listItem.findViewById(R.id.created_date);
        date.setText(trip.getDate());
        TextView start = listItem.findViewById(R.id.txt_start_address);
        start.setText(trip.getStart());
        TextView end = listItem.findViewById(R.id.txt_end_address);
        if(trip.getEnd().equals("")) {
            end.setVisibility(View.GONE);
        } else {
            end.setText(trip.getEnd());
        }
        TextView price = listItem.findViewById(R.id.txt_offer_price);
        price.setText(trip.getPrice());
        TextView status = listItem.findViewById(R.id.trip_status);
        int strStatus = trip.getStatus();
        status.setText(trip.getStatusTxt());
        TextView type = listItem.findViewById(R.id.txt_trip_type);
        if(trip.getType() == 0) {
            type.setText(getContext().getResources().getString(R.string.string_trip));
        } else if (trip.getType() == 1) {
            type.setText(getContext().getResources().getString(R.string.string_service_payment));
        } else if (trip.getType() == 2) {
            type.setText(getContext().getResources().getString(R.string.string_restaurant));
        } else if (trip.getType() == 3) {
            type.setText(getContext().getResources().getString(R.string.string_supermarket));
        }
        if(strStatus == 1) {
            status.setTextColor(Color.parseColor("#009A1A"));
        } else {
            status.setTextColor(Color.parseColor("#cc0000"));
        }
        return listItem;
    }
}