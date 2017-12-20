package com.bignerdranch.android.rabotnik;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;


public class PostShowFragment extends Fragment{
    public static final String KEY_DATA = "KEY_DATA";
    TextView tvTitle, tvDescription, tvSallary, tvCity, tvCategory, tvEmail;
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
        tvEmail = (TextView) v.findViewById(R.id.tv_email);

        tvTitle.setText(mPoster.getTitle());
        tvDescription.setText(mPoster.getBody());
        tvCity.setText(mPoster.getCity());
        tvSallary.setText(mPoster.getSallary());
        tvCategory.setText(mPoster.getCategory());
        tvEmail.setText(mPoster.getEmail());

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.menu_show_post, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_send_email:
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] {mPoster.getEmail()});
                intent.putExtra(Intent.EXTRA_SUBJECT, mPoster.getTitle());
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                }
                break;
        }
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


}
