package com.fed.websocket;

import android.util.Log;

import com.fed.message.Message;
import com.fed.train.model.MultiRegression;
import com.fed.util.ByteBufferUtil;
import com.fed.message.Constant;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;


public class FedWebSocketClient extends WebSocketClient {


    private static final String TAG = "FedWebSocketClient";
    private static final Message clientMessage=new Message();
    private MultiRegression multiRegression = new MultiRegression();



    public FedWebSocketClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void send(String text) {
        super.send(text);
    }

    @Override
    public void send(ByteBuffer bytes) {
        super.send(bytes);
    }
    

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.i(TAG, "onOpen: " + handshakedata);
    }

    @Override
    public void onMessage(String message) {
        //接收服务端指令
        if (message.equals(Constant.ALL_CLIENT_START)) {

            Log.i(TAG, "onMessage: train start");
            //等待接收全局梯度
            while (!multiRegression.RECEIVED_GLOBAL) ;
            multiRegression.RECEIVED_GLOBAL = false;
            Log.i(TAG, "onMessage: RECEIVED_GLOBAL");

            //用全局模型更新本地模型
            try{
                multiRegression.execute();
            }catch(Exception e){
                e.printStackTrace();
            }


            //上传模型
            try {
                System.out.println("上传模型");
                clientMessage.setMessage(Constant.CLIENT,Constant.CLIENT_UPDATE_WEIGHT,null,multiRegression.getWeight());
                ByteBuffer byteBuffer = ByteBufferUtil.getByteBuffer(clientMessage);
                send(byteBuffer);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (message.equals(Constant.ALL_CLIENT_STOP)) {
            //服务器通知结束训练
            Log.i(TAG, "onMessage: train stop ");
        }


    }


    @Override
    public void onMessage(ByteBuffer message) {

        Log.i(TAG, "onMessage: reveiving global");

        Log.i(TAG, "onMessage: "+message);

        Object object = null;
        try {
            Log.i(TAG, "onMessage: "+message);
            if(message==null){
                Log.e(TAG, "onMessage: message==null");
            }else {
                Log.i(TAG, "onMessage: ByteBufferUtil.getObject(message)");
                object = ByteBufferUtil.getObject(message);
            }
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }


        Message tempMessage=(Message)object;
        Log.i(TAG, "onMessage: 更新本地模型");


        assert tempMessage != null;
        if(tempMessage.owner==Constant.SERVER){
            switch (tempMessage.order){
                case Constant.SERVER_GLOBAL_MODEL:
                    Log.i(TAG, "onMessage: SERVER_GLOBAL_MODEL");
                    multiRegression.updateModel(tempMessage.model);
                    break;
                case Constant.SERVER_GLOBAL_WEIGHT:
                    Log.i(TAG, "onMessage: SERVER_GLOBAL_WEIGHT");
                    multiRegression.updateModel(tempMessage.weight);
                    break;
                default:
                    Log.i(TAG, "onMessage: \n"+tempMessage);
                    break;
            }
            Log.i(TAG, "onMessage: RECEIVED_GLOBAL = true");
            multiRegression.RECEIVED_GLOBAL = true;
        }


    }


    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.i(TAG, "onClose: " + code + " " + reason);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }
}
