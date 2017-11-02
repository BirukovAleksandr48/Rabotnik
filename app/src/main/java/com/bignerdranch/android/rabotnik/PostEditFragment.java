package com.bignerdranch.android.rabotnik;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class PostEditFragment extends Fragment {
    public static final String KEY_DATA = "KEY_DATA";
    String[] data;
    EditText etTitle, etDescription, etSallary, etCity;
    Spinner mSpinner;
    Poster mPoster;
    public static Handler handler;

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
        handler = new MyHandler();
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

        Intent i = new Intent(getContext(), MyService.class);
        i.putExtra(MyService.KEY_COMMAND_TYPE, MyService.KEY_COMMAND_GET_CATEGORIES);
        i.putExtra(MyService.SENDER, MyService.SENDER_PEF);
        getActivity().startService(i);


        return v;
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
    }

    public class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Log.e("MyLog", "handleMessage");
            super.handleMessage(msg);
            int what = msg.what;

            if(what == MyService.KEY_RETURN_CATEGORIES){
                Log.e("MyLog", "categoriest returned");
                Bundle bundle = msg.getData();
                String jsonString = bundle.getString(MyService.KEY_MESSAGE_TO_SERVER);

                Type listType = new TypeToken<ArrayList<String>>(){}.getType();
                ArrayList<String> arrayList = new Gson().fromJson(jsonString, listType);
                data = new String[arrayList.size()];
                Log.e("MyLog", mPoster.getCategory());
                int curCat = -1;
                for(int i = 0; i < arrayList.size(); i++){
                    data[i] = arrayList.get(i);
                    if(mPoster.getCategory() == arrayList.get(i)){
                        curCat = i;
                    }
                    Log.e("MyLog", data[i]);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, data);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSpinner.setAdapter(adapter);
                if(curCat != -1)
                    mSpinner.setSelection(curCat);
            }
        }
    }
}
