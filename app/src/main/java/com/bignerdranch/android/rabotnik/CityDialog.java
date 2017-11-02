package com.bignerdranch.android.rabotnik;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;

public class CityDialog extends DialogFragment{
    public static final String KEY_RESULT = "KEY_RESULT";
    EditText et;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_city, null);
        et = (EditText) v.findViewById(R.id.et_city);

        builder.setView(v)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    Intent intent = new Intent();
                    intent.putExtra(KEY_RESULT, et.getText().toString());
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                    dismiss();
                }
            })
            .setNegativeButton("Назад", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    CityDialog.this.getDialog().cancel();
                }
            })
            .setTitle("Введите название города");
        return builder.create();
    }
}
