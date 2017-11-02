package com.bignerdranch.android.rabotnik;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

public class SignInFragment extends Fragment{
    EditText etLogin, etPassword;
    Button btnSignIn, btnSignUp;
    TextView tvNotFound;

    public static Handler handler;

    public static SignInFragment newInstance(){
        Log.e("MyLog", "newInstance");
        SignInFragment fragement = new SignInFragment();
        return fragement;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e("MyLog", "onCreateFragment");
        super.onCreate(savedInstanceState);
        handler = new MyHandler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_sign_in, container, false);

        btnSignIn = (Button) v.findViewById(R.id.button_signin);
        btnSignUp = (Button) v.findViewById(R.id.button_signup);
        etLogin = (EditText) v.findViewById(R.id.et_login);
        etPassword = (EditText) v.findViewById(R.id.et_pass);
        tvNotFound = (TextView) v.findViewById(R.id.tv_not_found);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvNotFound.setVisibility(View.INVISIBLE);

                User user = new User(0, null, etLogin.getText().toString(), etPassword.getText().toString(), "user");
                String jsonData = new Gson().toJson(user);
                MesToServer mts = new MesToServer(MyService.KEY_COMMAND_SIGN_IN, jsonData);
                String jsonMes = new Gson().toJson(mts);

                Intent i = new Intent(getActivity(), MyService.class);
                i.putExtra(MyService.KEY_MESSAGE_TO_SERVER, jsonMes);
                i.putExtra(MyService.SENDER, MyService.SENDER_SIF);
                getActivity().startService(i);
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment f = SignUpFragment.newInstance();
                SignActivity activity = (SignActivity) getActivity();
                activity.setFragment(f);
            }
        });

        tvNotFound.setVisibility(View.INVISIBLE);

        return v;
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
                    Log.e("MyLog", "1");
                    PreferenceManager.getDefaultSharedPreferences(getActivity())
                            .edit()
                            .putInt(SignActivity.PREF_CURRENT_USER, user.getId())
                            .apply();
                    Log.e("MyLog", "2");
                    Intent intent = MainActivity.newIntent(getActivity(), data);
                    startActivity(intent);
                    Log.e("MyLog", "3");
                }else{                                      //значит не нашли
                    tvNotFound.setVisibility(View.VISIBLE);
                    etPassword.setText("");
                }
            }else if(what == MyService.KEY_CONNECTED){

            }
        }
    }
}
