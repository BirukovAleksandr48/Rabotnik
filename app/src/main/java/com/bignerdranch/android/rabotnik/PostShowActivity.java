package com.bignerdranch.android.rabotnik;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


public class PostShowActivity extends AppCompatActivity{
    private static final String JSON_POST = "JSON_POST";

    public static Intent newIntent(Context context, String jsonPost) {
        Intent i = new Intent(context, PostShowActivity.class);
        i.putExtra(JSON_POST, jsonPost);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_edit);
        Log.e("MyLog", "Создал showActivity");
        String data = getIntent().getStringExtra(JSON_POST);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.container_post_edit);
        if (fragment == null) {
            fragment = PostShowFragment.newInstance(data);
            fm.beginTransaction()
                    .add(R.id.container_post_edit, fragment)
                    .commit();
        }
    }
}
