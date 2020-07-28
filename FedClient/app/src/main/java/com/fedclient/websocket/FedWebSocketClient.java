package com.fedclient.websocket;

import android.util.Log;

import com.fedclient.Util.ByteBufferUtil;
import com.fedclient.train.model.MultiRegression;

import org.deeplearning4j.nn.gradient.DefaultGradient;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Map;


public class FedWebSocketClient extends WebSocketClient {


    public static final String START_CODE = "start_MultiRegression";
    public static final String STOP_CODE = "stop_MultiRegression";
    private static final String TAG = "FedWebSocketClient";
    private MultiRegression multiRegression = new MultiRegression();


    public FedWebSocketClient(URI serverUri) {
        super(serverUri);
    }

    public FedWebSocketClient(URI serverUri, Draft protocolDraft) {
        super(serverUri, protocolDraft);
    }

    public FedWebSocketClient(URI serverUri, Map<String, String> httpHeaders) {
        super(serverUri, httpHeaders);
    }

    public FedWebSocketClient(URI serverUri, Draft protocolDraft, Map<String, String> httpHeaders) {
        super(serverUri, protocolDraft, httpHeaders);
    }

    public FedWebSocketClient(URI serverUri, Draft protocolDraft, Map<String, String> httpHeaders, int connectTimeout) {
        super(serverUri, protocolDraft, httpHeaders, connectTimeout);
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
        if (message.equals(START_CODE)) {
            try {
                Log.i(TAG, "onMessage: startService " + START_CODE);
                //等待接收全局梯度
                while (!multiRegression.RECEIVED_GLOBAL) ;
                multiRegression.RECEIVED_GLOBAL = false;
                Log.i(TAG, "onMessage: RECEIVED_GLOBAL");

                //用全局模型更新本地模型
                //multiRegression.updateModel();
                multiRegression.execute();

                //上传梯度
                try {
                    ByteBuffer byteBuffer = ByteBufferUtil.getByteBuffer(multiRegression.getModel().gradient());
                    send(byteBuffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                Log.e(TAG, "onMessage: local train failed");
                e.printStackTrace();
            }
        } else if (message.equals(STOP_CODE)) {
            //训练结束，服务器通知结束训练
            Log.i(TAG, "onMessage: train stop " + STOP_CODE);
        }


    }


    @Override
    public void onMessage(ByteBuffer message) {

        Log.i(TAG, "onMessage: reveiving global");
        if (message.position() != 0) {
            //接收梯度

            Object gradient = null;
            try {
                gradient = ByteBufferUtil.getObject(message);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //更新梯度
            multiRegression.updateGradient((DefaultGradient) gradient);
            //更新模型
            multiRegression.updateModel();
        } else {
            System.out.println(message);
        }
        multiRegression.RECEIVED_GLOBAL = true;

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
