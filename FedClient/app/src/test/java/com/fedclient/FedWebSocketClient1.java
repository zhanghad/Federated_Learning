package com.fedclient;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.fedclient.train.service.MultiRegressionService;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;


public class FedWebSocketClient1 extends Service {

    public static final String START_CODE="start_MultiRegression";
    public static final String STOP_CODE="stop_MultiRegression";
//    private final String TAG = FedWebSocketClient.class.getSimpleName();
    private static final String TAG = "FedWebSocketClient";
    private static FedWebSocketClient1 clientInstance;
    private OkHttpClient CLIENT ;
    private WebSocket mWebSocket;
    private Intent serviceIntent;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        clientInstance = new FedWebSocketClient1();
        System.out.println(FedWebSocketClient1.this);
        serviceIntent=new Intent(FedWebSocketClient1.this,MultiRegressionService.class);
        Log.d(TAG, "onStartCommand: ");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        close(1001,"quit");


        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
    }

    //每个客户端只有一个websocket实例，通过该方法获取
    public static FedWebSocketClient1 getDefault() {
        return clientInstance;
    }


    public FedWebSocketClient1() {
        CLIENT = new OkHttpClient.Builder()
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .build();
    }



    public void connect(String url){
        Log.i(TAG, "connect: ");
        if (mWebSocket != null) {
            mWebSocket.cancel();
        }
        Request request = new Request.Builder()
                .url(url)
                .build();
        mWebSocket = CLIENT.newWebSocket(request,new SocketListener());
        Log.i(TAG, "connect: ");
    }

    public void sendMessage(String message){
        mWebSocket.send(message);
    }

    public void sendMessage(byte[] data){
        ByteString bs = ByteString.of(data);
        mWebSocket.send(bs);
    }


    public void close(int code, String reason){
        Log.i(TAG, "close: ");

        if(mWebSocket!=null){
            mWebSocket.close(code,reason);
        }
        else {
            Log.d(TAG, "close: mWebSocket == null");
        }

    }

    class SocketListener extends WebSocketListener {

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            super.onOpen(webSocket, response);
            Log.i(TAG,"onOpen response="+response);
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            super.onMessage(webSocket, text);
            Log.i(TAG,"onMessage text="+text);

/*            if(serviceIntent==null){
                System.out.println(serviceIntent);
                System.out.println(FedWebSocketClient.this);
                serviceIntent=new Intent(FedWebSocketClient.this,MultiRegressionService.class);
            }*/

            //接收服务端指令
            if(text.equals(START_CODE)){
                Log.i(TAG, "onMessage: startService");
                System.out.println(FedWebSocketClient1.this);
                startService(serviceIntent);
            }
            else if(text.equals(STOP_CODE)){
                //训练结束，服务器通知结束服务
                Log.i(TAG, "onMessage: stopService");
                stopService(serviceIntent);
            }

        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString message) {
            super.onMessage(webSocket, message);
            //接受全局梯度
            INDArray gradient= Nd4j.fromByteArray(message.toByteArray());

            Log.i(TAG,"onMessage bytes="+message);
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            super.onClosing(webSocket, code, reason);
            Log.i(TAG,"onClosing code="+code);
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            super.onClosed(webSocket, code, reason);
            Log.i(TAG,"onClosed code="+code);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            super.onFailure(webSocket, t, response);
            Log.e(TAG,"onFailure t="+t.getMessage());
            t.printStackTrace();
        }
    }

}
