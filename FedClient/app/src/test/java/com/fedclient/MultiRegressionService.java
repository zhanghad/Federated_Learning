package com.fedclient;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.fed.train.model.MultiRegression;


import org.nd4j.linalg.factory.Nd4j;

import java.io.IOException;

public class MultiRegressionService extends Service {
    private static final String TAG = "MultiRegressionService";


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
        Log.d(TAG, "onStartCommand: ");

        //创建模型
        MultiRegression multiRegression=new MultiRegression();

        //训练模型
        multiRegression.execute();
        //向服务器传递梯度
        try {
            FedWebSocketClient1.getDefault().sendMessage(Nd4j.toByteArray(multiRegression.getModel().gradient().gradient()));
        } catch (IOException e) {
            Log.e(TAG, "onStartCommand: ");
            e.printStackTrace();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {

        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
    }
}
