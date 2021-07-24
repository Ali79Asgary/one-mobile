package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class MoreListAdapter extends ArrayAdapter<More> {

    private Context mContext;
    int mResource;
    public MoreListAdapter(@NonNull Context context, int resource, @NonNull List<More> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        try {
            String name = getItem(position).getMoreName();
            int imageId = getItem(position).getImageId();

            More more = new More(name, imageId);
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);

            TextView textViewName = convertView.findViewById(R.id.moreNameTextView);
            textViewName.setText(name);

            ImageView imageViewPayment = convertView.findViewById(R.id.moreImageView);
            imageViewPayment.setImageResource(imageId);
        } catch (Exception e){
            e.printStackTrace();
        }
        return convertView;
    }
}
