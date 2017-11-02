package com.bignerdranch.android.rabotnik;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

import static android.R.attr.path;
import static android.R.layout.simple_list_item_1;

public class ListDialog extends DialogFragment {
    public static final String ARG_ALL_ITEMS = "ARG_ALL_ITEMS";
    public static final String KEY_RESULT = "KEY_RESULT";
    public MediaPlayer mMediaPlayer;
    private ArrayList<String> mCategories;

    public static ListDialog newInstance(ArrayList<String> arrayList){
        Bundle args = new Bundle();
        args.putSerializable(ARG_ALL_ITEMS, arrayList);
        ListDialog dialog = new ListDialog();
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mCategories = (ArrayList<String>) getArguments().getSerializable(ARG_ALL_ITEMS);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_list, null);
        ListView listView = (ListView) v.findViewById(R.id.list_downloads);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), simple_list_item_1, mCategories );
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra(KEY_RESULT, position);
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                dismiss();
            }
        });
        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .create();
    }
}
