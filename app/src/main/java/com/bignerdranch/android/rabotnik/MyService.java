package com.bignerdranch.android.rabotnik;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class MyService extends Service {
    private volatile Socket socket;
    public final static String KEY_COMMAND_TYPE = "KEY_COMMAND_TYPE";
    public final static String KEY_COMMAND_GET_RESUMES = "KEY_COMMAND_GET_RESUMES";
    public final static String KEY_COMMAND_FIND_RESUMES = "KEY_COMMAND_FIND_RESUMES";
    public final static String KEY_COMMAND_GET_USER = "KEY_COMMAND_GET_USER";
    public static final String KEY_JSON_RESULT = "KEY_JSON_RESULT";
    public static final String KEY_COMMAND_GET_CATEGORIES = "KEY_COMMAND_GET_CATEGORIES";
    public static final String KEY_MESSAGE_TO_SERVER = "KEY_MESSAGE_TO_SERVER";
    public static final String KEY_COMMAND_ADD_USER = "KEY_MESSAGE_TO_SERVER";
    public static final String KEY_COMMAND_SIGN_IN = "KEY_COMMAND_SIGN_IN";

    public static final int KEY_UPDATE = 1111;
    public static final int KEY_CONNECTED = 2222;
    public static final int KEY_RETURN_CATEGORIES = 3333;
    public static final int KEY_RETURN_USER = 4444;

    public final static String SENDER = "SENDER";
    public final static String SENDER_WF = "SENDER_WF";
    public final static String SENDER_PEF = "SENDER_PEF";
    public static final String SENDER_SIF = "SENDER_SIF";
    public static final String SENDER_SUF = "SENDER_SUF";
    public static final String SENDER_SA = "SENDER_SA";

    private String CurSender = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("MyLog", "onCreateMessangerService");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.e("MyLog", "connect");
                    int serverPort = 6666;

                    String address = "192.168.1.6";
                    try {
                        InetAddress ipAddress = InetAddress.getByName(address);
                        socket = new Socket(ipAddress, serverPort);
                    // Подключились
                        if(socket.isConnected()){
                            Message mesToActivity = SignActivity.handler.obtainMessage();
                            mesToActivity.what = KEY_CONNECTED;
                            SignActivity.handler.sendMessage(mesToActivity);
                        }

                        Scanner sc = new Scanner(socket.getInputStream());
                        while (sc.hasNextLine()) {
                            String sender = sc.nextLine();
                            String jsonMessage = sc.nextLine();
                            Log.e("MyLog", "Got a message from server. Length = " + String.valueOf(jsonMessage.length()));
                            Log.e("MyLog", sender);
                            Log.e("MyLog", jsonMessage);

                            MesToServer mts = new Gson().fromJson(jsonMessage, MesToServer.class);
                            String jsonString = mts.getJSONData();

                            if(sender.equals(SENDER_WF)){
                                Message mes = WorkerFragment.handler.obtainMessage();

                                if(mts.getCommand().equals(KEY_COMMAND_GET_RESUMES)
                                || mts.getCommand().equals(KEY_COMMAND_FIND_RESUMES)){
                                    mes.what = KEY_UPDATE;
                                }else if(mts.getCommand().equals(KEY_COMMAND_GET_CATEGORIES)){
                                    mes.what = KEY_RETURN_CATEGORIES;
                                }

                                Bundle bundle = new Bundle();
                                bundle.putString(KEY_JSON_RESULT, jsonString);
                                mes.setData(bundle);
                                WorkerFragment.handler.sendMessage(mes);
                            }else if(sender.equals(SENDER_PEF)){
                                Message mes = PostEditFragment.handler.obtainMessage();
                                mes.what = KEY_RETURN_CATEGORIES;
                                Bundle bundle = new Bundle();
                                bundle.putString(KEY_JSON_RESULT, jsonString);
                                mes.setData(bundle);
                                PostEditFragment.handler.sendMessage(mes);
                            }else if(sender.equals(SENDER_SIF)){
                                Message mes = SignInFragment.handler.obtainMessage();
                                mes.what = KEY_RETURN_USER;
                                Bundle bundle = new Bundle();
                                bundle.putString(KEY_JSON_RESULT, jsonString);
                                mes.setData(bundle);
                                SignInFragment.handler.sendMessage(mes);
                            }else if(sender.equals(SENDER_SUF)){
                                Message mes = SignUpFragment.handler.obtainMessage();
                                mes.what = KEY_RETURN_USER;
                                Bundle bundle = new Bundle();
                                bundle.putString(KEY_JSON_RESULT, jsonString);
                                mes.setData(bundle);
                                SignUpFragment.handler.sendMessage(mes);
                            }else if(sender.equals(SENDER_SA)){
                                Message mes = SignActivity.handler.obtainMessage();
                                mes.what = KEY_RETURN_USER;
                                Bundle bundle = new Bundle();
                                bundle.putString(KEY_JSON_RESULT, jsonString);
                                mes.setData(bundle);
                                SignActivity.handler.sendMessage(mes);
                            }

                            Log.e("MyLog", "Сообщение в активити отправлено.");
                        }
                    } catch (IOException e) {e.printStackTrace();}
                } catch (Exception e) {e.printStackTrace();}
            }
        }).start();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        if(intent != null) {
            Log.e("MyLog", "onStartCommand");
            final String sender = intent.getStringExtra(SENDER);
            final String message = intent.getStringExtra(KEY_MESSAGE_TO_SERVER);

            if (message == null)
                return START_NOT_STICKY;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (socket == null || socket.isClosed()) {
                        Log.e("MyLog", "Невозможно отправить данные. Сокет не создан или закрыт");
                        return;
                    }
                    try {
                        PrintWriter pw = new PrintWriter(socket.getOutputStream());
                        pw.write(sender + "\n");
                        pw.write(message + "\n");
                        pw.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.e("MyLog", "onDestroy");
        try {
            if(!socket.isClosed())
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }


}
