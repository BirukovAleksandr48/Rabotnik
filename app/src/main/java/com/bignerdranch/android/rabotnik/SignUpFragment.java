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

public class SignUpFragment extends Fragment{
    EditText etLogin, etPassword, etName, etPassword2;
    Button btnSignUp, btnBack;
    TextView tvPasswordNotEqual;

    public static Handler handler;

    public static SignUpFragment newInstance(){
        SignUpFragment fragement = new SignUpFragment();

        return fragement;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new MyHandler();
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_sign_up, container, false);

        btnSignUp = (Button) v.findViewById(R.id.button_signup);
        btnBack = (Button) v.findViewById(R.id.button_back);
        etLogin = (EditText) v.findViewById(R.id.et_login);
        etPassword = (EditText) v.findViewById(R.id.et_pass);
        etName = (EditText) v.findViewById(R.id.et_name);
        etPassword2 = (EditText) v.findViewById(R.id.et_pass2);
        tvPasswordNotEqual = (TextView) v.findViewById(R.id.tv_not_found);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvPasswordNotEqual.setVisibility(View.INVISIBLE);

                if(!etPassword.getText().toString().equals(etPassword2.getText().toString())){
                    tvPasswordNotEqual.setVisibility(View.VISIBLE);
                    return;
                }

                User user = new User(0, etName.getText().toString(), etLogin.getText().toString(), etPassword.getText().toString(), "user");
                String jsonData = new Gson().toJson(user);
                Log.e("MyLog", jsonData);
                MesToServer mts = new MesToServer(MyService.KEY_COMMAND_ADD_USER, jsonData);
                String jsonMes = new Gson().toJson(mts);
                Log.e("MyLog", jsonMes);
                Intent i = new Intent(getActivity(), MyService.class);
                i.putExtra(MyService.KEY_MESSAGE_TO_SERVER, jsonMes);
                i.putExtra(MyService.SENDER, MyService.SENDER_SUF);
                getActivity().startService(i);
            }
        });

        tvPasswordNotEqual.setVisibility(View.INVISIBLE);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment f = SignInFragment.newInstance();
                SignActivity activity = (SignActivity) getActivity();
                activity.setFragment(f);
            }
        });

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
                if(user.getId() > 0){                       //значит такого пользователя добавили успешно

                    PreferenceManager.getDefaultSharedPreferences(getActivity())
                            .edit()
                            .putInt(SignActivity.PREF_CURRENT_USER, user.getId())
                            .apply();

                    Intent intent = MainActivity.newIntent(getActivity(), data);
                    startActivityForResult(intent, ((SignActivity)getActivity()).request);
                }else{                                      //значит не добавили
                    etPassword.setText("");
                }
            }
        }
    }
}
