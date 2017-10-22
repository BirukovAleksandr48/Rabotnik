package com.bignerdranch.android.rabotnik;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.gson.Gson;

import java.util.ArrayList;

public class PostEditFragment extends Fragment {
    public static final String KEY_DATA = "KEY_DATA";
    String[] data = {"one", "two", "three", "four", "five"};
    EditText etTitle, etDescription, etSallary, etCity;
    Spinner mSpinner;
    Poster mPoster;

    public static PostEditFragment newInstance(String data){
        PostEditFragment fragement = new PostEditFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_DATA, data);
        fragement.setArguments(bundle);
        return fragement;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("MyLog", "onCreate");

        String jsonString = getArguments().getString(KEY_DATA);
        if(jsonString != null){
            mPoster = new Gson().fromJson(jsonString, Poster.class);
        }else{
            mPoster = new Poster();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("MyLog", "onCreateView");
        View v = inflater.inflate(R.layout.post_edit_layout, container, false);

        etTitle = (EditText) v.findViewById(R.id.et_title);
        etDescription = (EditText) v.findViewById(R.id.et_description);
        etCity = (EditText) v.findViewById(R.id.et_city);
        etSallary = (EditText) v.findViewById(R.id.et_sallary);
        mSpinner = (Spinner) v.findViewById(R.id.spinner_category);

        etTitle.setText(mPoster.getTitle());
        etDescription.setText(mPoster.getBody());
        etCity.setText(mPoster.getCity());
        etSallary.setText(mPoster.getSallary());

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        return v;
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
    }


}
