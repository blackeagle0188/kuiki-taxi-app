package com.mobiledevteam.kuiki.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.mobiledevteam.kuiki.R;

import java.util.ArrayList;
import java.util.List;

public class ReviewHistoryAdapter extends ArrayAdapter<ReviewHistory> {
    private Context mContext;
    private List<ReviewHistory> allEventList = new ArrayList<>();

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    public ReviewHistoryAdapter(Context context, ArrayList<ReviewHistory> list) {
        super(context, 0 , list);
        mContext = context;
        allEventList = list;
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.review_history,parent,false);

        ReviewHistory review = allEventList.get(position);

        TextView date = listItem.findViewById(R.id.created_date);
        date.setText(review.getDate());
        TextView comment = listItem.findViewById(R.id.txt_review);
        comment.setText(review.getComment());
        RatingBar star = listItem.findViewById(R.id.star_review);
        star.setRating(review.getStar());
        return listItem;
    }
}