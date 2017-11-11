package com.bignerdranch.android.rabotnik;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class FragmentUserProfile extends Fragment{
    User mUser;

    public static FragmentUserProfile newInstance(){
        FragmentUserProfile fragement = new FragmentUserProfile();
        return fragement;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mUser = ((MainActivity) getActivity()).getUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.user_profile, container, false);
        ImageView avatar = (ImageView) v.findViewById(R.id.iv_avatar);
        avatar.setImageResource(R.drawable.avatar);
        TextView tv_name = (TextView) v.findViewById(R.id.user_name);
        TextView tv_login = (TextView) v.findViewById(R.id.user_login);

        tv_name.setText(mUser.getName());
        tv_login.setText(mUser.getLogin());

        return v;
    }

}
