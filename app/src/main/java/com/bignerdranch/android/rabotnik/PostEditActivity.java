package com.bignerdranch.android.rabotnik;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import java.util.UUID;

public class PostEditActivity extends AppCompatActivity {
    private static final String JSON_POST = "JSON_POST";
    private static final String POST_TYPE = "POST_TYPE";

    public static Intent newIntent(Context context, String jsonPost, int postType) {
        Intent i = new Intent(context, PostEditActivity.class);
        i.putExtra(JSON_POST, jsonPost);
        i.putExtra(POST_TYPE, postType);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_edit);

        String data = getIntent().getStringExtra(JSON_POST);
        int type = getIntent().getIntExtra(POST_TYPE, -1);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.container_post_edit);
        if (fragment == null) {
            fragment = PostEditFragment.newInstance(data, type);
            fm.beginTransaction()
                    .add(R.id.container_post_edit, fragment)
                    .commit();
        }
    }
}
