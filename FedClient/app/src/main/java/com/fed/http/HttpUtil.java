package com.fed.http;

import org.deeplearning4j.nn.gradient.Gradient;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpUtil {

    public static void httpRequestGet(String address,okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30,TimeUnit.SECONDS)
                .build();

        Request request=new Request.Builder()
                .url(address)
                .build();
        client.newCall(request).enqueue(callback);
    }


    public static void httpRequestPost(String address, byte[] grad, okhttp3.Callback callback){
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        MediaType GRAD = MediaType.parse("application/plain");

        OkHttpClient client=new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30,TimeUnit.SECONDS)
                .build();
        RequestBody requestBody=RequestBody.create(grad,GRAD);
        Request request=new Request.Builder()
                .url(address)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

}
