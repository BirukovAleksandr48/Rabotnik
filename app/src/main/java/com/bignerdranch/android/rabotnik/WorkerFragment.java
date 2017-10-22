package com.bignerdranch.android.rabotnik;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TabHost;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class WorkerFragment extends Fragment {
    ListView lvMine, lvFind;
    ArrayList <Poster> mArrayList;
    ImageButton btnAdd;
    public static Handler handler;
    ItemAdapter adapter;

    public static WorkerFragment newInstance(){
        WorkerFragment fragement = new WorkerFragment();
        return fragement;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("MyLog", "onCreate");
        mArrayList = new ArrayList<>();

        handler = new MyHandler();

        Intent i = new Intent(getContext(), MyService.class);
        getActivity().startService(i);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("MyLog", "onCreateView");
        View v = inflater.inflate(R.layout.resume_layout, container, false);

        TabHost tabHost = (TabHost) v.findViewById(R.id.resume_tabhost);
        tabHost.setup();
        TabHost.TabSpec tabSpec = tabHost.newTabSpec("tag_all");
        tabSpec.setContent(R.id.tab1);
        tabSpec.setIndicator("Find");
        tabHost.addTab(tabSpec);
        tabSpec = tabHost.newTabSpec("tag_favorite");
        tabSpec.setContent(R.id.tab2);
        tabSpec.setIndicator("Favorite");
        tabHost.addTab(tabSpec);
        tabSpec = tabHost.newTabSpec("tag_mine");
        tabSpec.setContent(R.id.tab3);
        tabSpec.setIndicator("Mine");
        tabHost.addTab(tabSpec);
        tabHost.setCurrentTab(0);

        lvFind = (ListView) v.findViewById(R.id.list_all);
        adapter = new ItemAdapter(getContext(), mArrayList);
        lvFind.setAdapter(adapter);

        btnAdd = (ImageButton) v.findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = PostEditActivity.newIntent(getActivity(), null);
                startActivity(i);
            }
        });

        return v;
    }

    public class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Log.e("MyLog", "handleMessage");
            super.handleMessage(msg);
            int what = msg.what;

            if(what == MyService.KEY_UPDATE){
                Bundle bundle = msg.getData();
                String jsonString = bundle.getString(MyService.KEY_JSONSTRING);

                Type listType = new TypeToken<ArrayList<Poster>>(){}.getType();
                mArrayList= new Gson().fromJson(jsonString, listType);

                updateList();
            }else if(what == MyService.KEY_CONNECTED){
                Intent i = new Intent(getContext(), MyService.class);
                i.putExtra(MyService.KEY_COMMAND_TYPE, MyService.KEY_COMMAND_GET_RESUMES);
                getActivity().startService(i);
            }
        }
    }
    public void updateList(){
        Log.e("MyLog", "updateList. Size of Array:" + String.valueOf(mArrayList.size()));
        adapter = new ItemAdapter(getContext(), mArrayList);
        lvFind.setAdapter(adapter);
    }
}
