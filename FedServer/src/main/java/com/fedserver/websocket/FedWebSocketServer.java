package com.fedserver.websocket;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.fedserver.Util.ByteBufferUtil;
import org.deeplearning4j.nn.gradient.DefaultGradient;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;


//每一个任务均会创建一个FedWebSocketServer 对象
public class FedWebSocketServer extends WebSocketServer {


    private final ConcurrentHashMap<WebSocket,ClientInfo> webSocketSet = new ConcurrentHashMap<WebSocket,ClientInfo>();//在线连接
    private int onlineClientCount;//在线连接数
    //private final List<Map<String,INDArray>> updateWeight=new ArrayList<Map<String,INDArray>>();//更新的梯度的集合
    private final List<MultiLayerNetwork> updateModel=new ArrayList<MultiLayerNetwork>();

    public int getUpdateModelNum(){
        return updateModel.size();
    }

    public List<MultiLayerNetwork> getUpdateModel() {
        return updateModel;
    }

    public void clearUpdateModel(){
        updateModel.clear();
    }


    public int getOnlineClientCount(){
        return onlineClientCount;
    }

    public ConcurrentHashMap<WebSocket,ClientInfo> getWebSocketSet(){
        return webSocketSet;
    }

/*    public int getUpdateWeightNum(){
        return updateWeight.size();
    }

    public List<Map<String,INDArray>> getUpdateWeight(){
        return updateWeight;
    }

    public void clearUpdateWeight(){
        updateWeight.clear();
    }*/

    public void printAllClient(){
        Set<WebSocket> keySet=webSocketSet.keySet();
        for (WebSocket key : keySet) {
            System.out.println(webSocketSet.get(key).getClientHandshake().toString());
        }
    }



    public FedWebSocketServer(int port) throws UnknownHostException {
        super(new InetSocketAddress(port));
        this.start();
        System.out.println("FedSocketServer started on port: " + this.getPort());
    }

    public FedWebSocketServer(InetSocketAddress address) {
        super(address);
    }

    public FedWebSocketServer(int port, Draft_6455 draft) {
        super(new InetSocketAddress(port), Collections.<Draft>singletonList(draft));
    }


    @Override
    public void onStart() {
        System.out.println("FedWebSocketServer started!");
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }


    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        conn.send("Welcome to the FedServer!");
        System.out.println(conn.getRemoteSocketAddress().getAddress().getHostAddress() + " Linked to FedServer!");

        ClientInfo clientInfo=new ClientInfo();
        clientInfo.setClientHandshake(handshake);
        webSocketSet.put(conn,clientInfo);
        onlineClientCount++;
        System.out.println(onlineClientCount+" client(s) now");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println(conn + "WebSocket closed!");
        webSocketSet.remove(conn);
        onlineClientCount--;
        System.out.println(onlineClientCount+" client(s) now");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {

        System.out.println(conn + ": " + message);
    }

    @lombok.SneakyThrows
    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {
        //第一个更新到达
        WebSocketController.FIRST_UPDATE=true;

        //INDArray gradient=Nd4j.fromByteArray(message.array());
        Object model= ByteBufferUtil.getObject(message);

        //updateWeight.add((HashMap<String,INDArray>) weight);

        updateModel.add((MultiLayerNetwork)model);

        System.out.println(conn + " update model : \n" + model);

    }



    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
        if (conn != null) {
            // some errors like port binding failed may not be assignable to a specific websocket
        }
    }



}
