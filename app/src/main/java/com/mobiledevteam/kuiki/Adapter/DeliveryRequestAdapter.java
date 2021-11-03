package com.mobiledevteam.kuiki.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mobiledevteam.kuiki.Common;
import com.mobiledevteam.kuiki.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DeliveryRequestAdapter extends ArrayAdapter<DeliveryRequest> {

    private Context mContext;
    private List<DeliveryRequest> allEventList = new ArrayList<>();

    public DeliveryRequestAdapter(Context context, ArrayList<DeliveryRequest> list) {
        super(context, 0, list);
        mContext = context;
        allEventList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.delivery_request,parent,false);

        DeliveryRequest offer = allEventList.get(position);

        ImageView clinic_image = (ImageView) listItem.findViewById(R.id.delivery_img_avatar);
        ImageView img_star = (ImageView) listItem.findViewById(R.id.img_star);
        img_star.setImageResource(R.drawable.ic_baseline_star_24);
        Picasso.get().load(Common.getInstance().getBaseURL()+"/backend/" + offer.getUserAvatar()).into(clinic_image);
        TextView name = (TextView) listItem.findViewById(R.id.txt_user_name);
        name.setText(offer.getUserName());
        TextView address = (TextView) listItem.findViewById(R.id.txt_address);
        address.setText(offer.getUserAddress());
        TextView review = (TextView) listItem.findViewById(R.id.txt_user_review);
        review.setText(offer.getReview());
        TextView delivery_type = (TextView) listItem.findViewById(R.id.txt_delivery_type);
        int type = offer.getType();
        switch (type) {
            case 0:
                delivery_type.setText(getContext().getResources().getString(R.string.string_service_payment));
                break;
            case 1:
                delivery_type.setText(getContext().getResources().getString(R.string.string_restaurant));
                break;
            case 2:
                delivery_type.setText(getContext().getResources().getString(R.string.string_supermarket));
                break;
        }
        return listItem;
    }
}
