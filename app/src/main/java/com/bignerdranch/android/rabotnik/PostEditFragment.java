package com.bignerdranch.android.rabotnik;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    public static final String KEY_TYPE = "KEY_TYPE";
    String[] data;
    EditText etTitle, etDescription, etSallary, etCity, etEmail;
    Spinner mSpinner;
    Poster mPoster;
    public static Handler handler;
    private boolean isChanged = false;
    private int postType;

    public static PostEditFragment newInstance(String data, int type){
        PostEditFragment fragement = new PostEditFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_DATA, data);
        bundle.putInt(KEY_TYPE, type);
        fragement.setArguments(bundle);
        return fragement;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        String jsonString = getArguments().getString(KEY_DATA);
        postType = getArguments().getInt(KEY_TYPE);
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
        etEmail = (EditText) v.findViewById(R.id.et_email);

        etTitle.setText(mPoster.getTitle());
        etDescription.setText(mPoster.getBody());
        etCity.setText(mPoster.getCity());
        etSallary.setText(mPoster.getSallary());
        etEmail.setText(mPoster.getEmail());

        TextWatcher tv = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        etTitle.addTextChangedListener(tv);
        etDescription.addTextChangedListener(tv);
        etCity.addTextChangedListener(tv);
        etSallary.addTextChangedListener(tv);
        etEmail.addTextChangedListener(tv);

        MesToServer mts = new MesToServer(MyService.KEY_COMMAND_GET_CATEGORIES, null);     //Теперь запрашиваем список категорий
        String jsonMes = new Gson().toJson(mts);
        Intent i = new Intent(getActivity(), MyService.class);
        i.putExtra(MyService.KEY_MESSAGE_TO_SERVER, jsonMes);
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
                Bundle bundle = msg.getData();
                String jsonString = bundle.getString(MyService.KEY_JSON_RESULT);
                Type listType = new TypeToken<ArrayList<String>>(){}.getType();
                ArrayList<String> arrayList = new Gson().fromJson(jsonString, listType);
                data = new String[arrayList.size()];
                int curCat = -1;
                for(int i = 0; i < arrayList.size(); i++){
                    data[i] = arrayList.get(i);
                    if(mPoster.getCategory() != null && mPoster.getCategory().equals(arrayList.get(i))){
                        curCat = i;
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, data);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSpinner.setAdapter(adapter);
                if(curCat != -1) {
                    mSpinner.setSelection(curCat);
                    isChanged = false;
                }
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.menu_edit_post, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_save_post:
                Log.e("MyLog", String.valueOf(isChanged));
                if(!isChanged)
                    break;
                AlertDialog.Builder ad = new AlertDialog.Builder(getContext());
                ad.setMessage("Вы уверены, что хотите сохранить изменения?"); // сообщение
                ad.setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        savePoster();
                    }
                });
                ad.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        dialog.cancel();
                    }
                });
                ad.show();
                break;
            case R.id.menu_item_delete_post:
                AlertDialog.Builder ad2 = new AlertDialog.Builder(getContext());
                ad2.setMessage("Вы уверены, что хотите удалить?"); // сообщение
                ad2.setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        deletePoster();
                    }
                });
                ad2.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        dialog.cancel();
                    }
                });
                ad2.show();
                break;
            case android.R.id.home: //нажата кнопка Up
                if(!isChanged){     //Проверяю изменил ли пользователь данные на этой странице
                    return false;   //Если нет - то ничего сохранять не нужно - просто выходим
                }
                // Если да - то спрашиваем нужно ли сохранить изменения
                AlertDialog.Builder dialogExit = new AlertDialog.Builder(getContext());
                dialogExit.setMessage("Сохранить изменения перед выходом?");
                dialogExit.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        savePoster();
                        exit();
                    }
                });
                dialogExit.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        dialog.cancel();
                        exit();
                    }
                });
                dialogExit.show();
        }
        return true;
    }
    public void exit(){
        getActivity().finish();
    }

    public void savePoster(){
        mPoster.setTitle(etTitle.getText().toString());
        mPoster.setBody(etDescription.getText().toString());
        mPoster.setSallary(etSallary.getText().toString());
        mPoster.setCity(etCity.getText().toString());
        mPoster.setCategory(data[mSpinner.getSelectedItemPosition()]);
        mPoster.setEmail(etEmail.getText().toString());

        String data = new Gson().toJson(mPoster);
        MesToServer mts;
        if(postType == 0){
            mts = new MesToServer(MyService.KEY_COMMAND_SAVE_RESUME, data);     //Теперь запрашиваем список категорий
        }else{
            mts = new MesToServer(MyService.KEY_COMMAND_SAVE_VACANCY, data);     //Теперь запрашиваем список категорий
        }

        String jsonMes = new Gson().toJson(mts);
        Intent i = new Intent(getActivity(), MyService.class);
        i.putExtra(MyService.KEY_MESSAGE_TO_SERVER, jsonMes);
        i.putExtra(MyService.SENDER, MyService.SENDER_PEF);
        getActivity().startService(i);
        isChanged = false;
    }
    public void deletePoster(){
        Log.e("MyLog", "сейчас удалю постер");
        String data2 = new Gson().toJson(mPoster);

        MesToServer mts2;
        if(postType == 0){
            mts2 = new MesToServer(MyService.KEY_COMMAND_DELETE_RESUME, data2);
        }else{
            mts2 = new MesToServer(MyService.KEY_COMMAND_DELETE_VACANCY, data2);     //Теперь запрашиваем список категорий
        }
        String jsonMes2 = new Gson().toJson(mts2);
        Intent i2 = new Intent(getActivity(), MyService.class);
        i2.putExtra(MyService.KEY_MESSAGE_TO_SERVER, jsonMes2);
        i2.putExtra(MyService.SENDER, MyService.SENDER_PEF);
        getActivity().startService(i2);

        getActivity().finish();
    }

}
