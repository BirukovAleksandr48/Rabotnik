package com.bignerdranch.android.rabotnik;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class EmployerFragment extends Fragment {
    public static EmployerFragment newInstance(){
        EmployerFragment fragement = new EmployerFragment();
        return fragement;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.vacancy_layout, container, false);



        return v;
    }
}
