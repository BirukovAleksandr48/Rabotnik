package com.bignerdranch.android.rabotnik;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;


public class PostShowFragment extends Fragment{
    public static final String KEY_DATA = "KEY_DATA";
    TextView tvTitle, tvDescription, tvSallary, tvCity, tvCategory;
    Poster mPoster;

    public static PostShowFragment newInstance(String data){
        PostShowFragment fragement = new PostShowFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_DATA, data);
        fragement.setArguments(bundle);
        return fragement;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Log.e("MyLog", "Создал showFragment");
        String jsonString = getArguments().getString(KEY_DATA);
        if(jsonString != null){
            mPoster = new Gson().fromJson(jsonString, Poster.class);
        }else{
            mPoster = new Poster();
        }
        Log.e("MyLog", mPoster.getCategory());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("MyLog", "отобразил showFragment");
        View v = inflater.inflate(R.layout.post_layout, container, false);

        tvTitle = (TextView) v.findViewById(R.id.tv_title);
        tvDescription = (TextView) v.findViewById(R.id.tv_description);
        tvCity = (TextView) v.findViewById(R.id.tv_city);
        tvSallary = (TextView) v.findViewById(R.id.tv_sallary);
        tvCategory = (TextView) v.findViewById(R.id.tv_category);

        tvTitle.setText(mPoster.getTitle());
        tvDescription.setText(mPoster.getBody());
        tvCity.setText(mPoster.getCity());
        tvSallary.setText(mPoster.getSallary());
        tvCategory.setText(mPoster.getCategory());

        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


}
