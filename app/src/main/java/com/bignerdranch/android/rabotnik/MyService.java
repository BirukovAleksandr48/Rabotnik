package com.bignerdranch.android.rabotnik;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class MyService extends Service {
    private volatile Socket socket;
    public final static String KEY_COMMAND_TYPE = "KEY_COMMAND_TYPE";
    public final static String KEY_COMMAND_GET_RESUMES = "KEY_COMMAND_GET_RESUMES";
    public static final String KEY_JSONSTRING = "KEY_JSONSTRING";
    public static final int KEY_UPDATE = 1111;
    public static final int KEY_CONNECTED = 2222;

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

                        if(socket.isConnected()){
                            Message mesToActivity = WorkerFragment.handler.obtainMessage();
                            mesToActivity.what = KEY_CONNECTED;
                            WorkerFragment.handler.sendMessage(mesToActivity);
                        }

                        Scanner sc = new Scanner(socket.getInputStream());
                        while (true) {
                            String jsonString = sc.nextLine();
                            Log.e("MyLog", "Got a message from server. Length = " + String.valueOf(jsonString.length()));

                            Message mesToActivity = WorkerFragment.handler.obtainMessage();
                            mesToActivity.what = KEY_UPDATE;
                            Bundle bundle = new Bundle();
                            bundle.putString(KEY_JSONSTRING, jsonString);
                            mesToActivity.setData(bundle);
                            WorkerFragment.handler.sendMessage(mesToActivity);

                            Log.e("MyLog", "Сообщение в активити отправлено.");
                        }
                    } catch (IOException e) {e.printStackTrace();}
                } catch (Exception e) {e.printStackTrace();}
            }
        }).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {
            Log.e("MyLog", "onStartCommand");

            final String command = intent.getStringExtra(KEY_COMMAND_TYPE);

            if (command != null && command.equals(KEY_COMMAND_GET_RESUMES)) {
                Log.e("MyLog", "if (command == KEY_COMMAND_GET_RESUMES)");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (socket == null || socket.isClosed()) {
                            Log.e("MyLog", "Невозможно отправить данные. Сокет не создан или закрыт");
                            return;
                        }
                        try {
                            PrintWriter pw = new PrintWriter(socket.getOutputStream());
                            pw.write(KEY_COMMAND_GET_RESUMES + "\n");
                            pw.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
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

    public void sendData(String data) {
        Log.e("MyLog", "sendData");

    }

}
