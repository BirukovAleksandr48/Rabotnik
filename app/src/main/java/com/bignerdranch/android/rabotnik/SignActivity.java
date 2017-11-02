package com.bignerdranch.android.rabotnik;


import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;

public class SignActivity extends AppCompatActivity{
    public static final String PREF_CURRENT_USER = "PREF_CURRENT_USER";
    public static Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e("MyLog", "onCreate");
        Intent i = new Intent(this, MyService.class);
        startService(i);

        handler = new MyHandler();
    }

    public int getAuthorizedUser(){
        Log.e("MyLog", "getAuthorizedUser");
        return PreferenceManager.getDefaultSharedPreferences(this).getInt(PREF_CURRENT_USER, 0);
    }
    public void setFragment(Fragment fragment){
        Log.e("MyLog", "setFragment");
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    public class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Log.e("MyLog", "handleMessage");
            super.handleMessage(msg);
            int what = msg.what;

            if(what == MyService.KEY_RETURN_USER){
                Bundle bundle = msg.getData();
                String data = bundle.getString(MyService.KEY_JSON_RESULT);
                User user = new Gson().fromJson(data, User.class);
                if(user.getId() > 0){                       //значит такого пользователя нашли
                    Intent intent = MainActivity.newIntent(getBaseContext(), data);
                    startActivity(intent);
                }else{                                      //значит не нашли
                    Fragment f = SignInFragment.newInstance();
                    setFragment(f);
                }
            }else if(what == MyService.KEY_CONNECTED){
                int id = getAuthorizedUser();
                if(id == 0){
                    Log.e("MyLog", "getAuthorizedUser() == 0");
                    Fragment f = SignInFragment.newInstance();
                    setFragment(f);
                }else{
                    Log.e("MyLog", "else");
                    User user = new User(id, null, null, null, null);
                    String jsonData = new Gson().toJson(user);
                    MesToServer mts = new MesToServer(MyService.KEY_COMMAND_GET_USER, jsonData);
                    String jsonMes = new Gson().toJson(mts);

                    Intent i2 = new Intent(getBaseContext(), MyService.class);
                    i2.putExtra(MyService.KEY_MESSAGE_TO_SERVER, jsonMes);
                    i2.putExtra(MyService.SENDER, MyService.SENDER_SA);
                    startService(i2);

                }
            }
        }
    }
}
