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

import org.w3c.dom.Text;

/**
 * Created by timfong224 on 3/13/14.
 * http://www.learn2crack.com/2013/10/android-custom-listview-images-text-example.html
 */
public class EventListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final Integer[] event_id;
    private final String[] title;
    private final String[] venue;
    private final String[] confirm_time;
    private final String[] image;
    public EventListAdapter(Activity context, Integer[] event_id, String[] title, String[] venue, String[] confirm_time, String[] image) {
        super(context, R.layout.list_single, title);
        this.context = context;
        this.event_id = event_id;
        this.title = title;
        this.venue = venue;
        this.confirm_time = confirm_time;
        this.image = image;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_single, null, true);

        TextView tvID = (TextView) rowView.findViewById(R.id.tvID);
        TextView tvTitle = (TextView) rowView.findViewById(R.id.tvTitle);
        TextView tvVenue = (TextView) rowView.findViewById(R.id.tvVenue);
        TextView tvDate = (TextView) rowView.findViewById(R.id.tvDate);
        //ImageView ivImage = (ImageView) rowView.findViewById(R.id.ivImage);

        tvID.setText(event_id[position].toString());
        tvTitle.setText(title[position]);
        tvVenue.setText(venue[position]);
        tvDate.setText(confirm_time[position]);
        //ivImage.setImageResource(imageId[position]);
        return rowView;
    }
}
