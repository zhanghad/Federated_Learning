package com.fed.websocket;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;


import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketService extends Service {

    private static final String TAG = "WebSocketController";
    public static final String wsUrl="ws://192.168.1.3:8887/";
    private FedWebSocketClient fedWebSocketClient;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: ");
        //实例化websocket
        try {
            Log.i(TAG, "onStartCommand: try");
            fedWebSocketClient=new FedWebSocketClient(new URI(wsUrl));
            fedWebSocketClient.connect();
        } catch (URISyntaxException e) {
            Log.e(TAG, "onStartCommand: " );
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");

        //关闭连接
        if(fedWebSocketClient==null){
            Log.d(TAG, "onDestroy: websocket is null");
        }else {
            fedWebSocketClient.close();
        }
        super.onDestroy();
    }

}
