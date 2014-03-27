package com.example.usingintent.app;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.LayerDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by timfong224 on 3/13/14.
 * http://www.learn2crack.com/2013/10/android-custom-listview-images-text-example.html
 */
public class EventListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] title;
    private final String[] venue;
    private final String[] confirm_time;
    private final Integer[] imageId;
    public EventListAdapter(Activity context, String[] title, String[] venue, String[] confirm_time, Integer[] imageId) {
        super(context, R.layout.list_single, title);
        this.context = context;
        this.title = title;
        this.venue = venue;
        this.confirm_time = confirm_time;
        this.imageId = imageId;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_single, null, true);

        TextView tvTitle = (TextView) rowView.findViewById(R.id.tvTitle);
        TextView tvVenue = (TextView) rowView.findViewById(R.id.tvVenue);
        TextView tvDate = (TextView) rowView.findViewById(R.id.tvDate);
        //ImageView ivImage = (ImageView) rowView.findViewById(R.id.ivImage);

        tvTitle.setText(title[position]);
        tvVenue.setText(venue[position]);
        tvDate.setText(confirm_time[position]);
        //ivImage.setImageResource(imageId[position]);
        return rowView;
    }
}
