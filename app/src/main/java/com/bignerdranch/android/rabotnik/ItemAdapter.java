package com.bignerdranch.android.rabotnik;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;

public class ItemAdapter extends BaseAdapter {

    Context ctx;
    LayoutInflater lInflater;
    ArrayList<Poster> objects;

    public ItemAdapter(Context context, ArrayList<Poster> items) {
        ctx = context;
        objects = items;
        lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.list_item, parent, false);
        }

        Poster o = getPoster(position);

        ((TextView) view.findViewById(R.id.tv_title)).setText(o.getTitle());
        ((TextView) view.findViewById(R.id.tv_city)).setText(o.getCity());
        ((TextView) view.findViewById(R.id.tv_sallary)).setText(o.getSallary());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String json = new Gson().toJson(getPoster(position));
                Log.e("MyLog", "jsonString = " + json);
                Intent i = PostEditActivity.newIntent(ctx, json);
                ctx.startActivity(i);
            }
        });
        return view;
    }

    Poster getPoster(int position) {
        return ((Poster) getItem(position));
    }

}
