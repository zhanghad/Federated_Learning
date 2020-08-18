package com.fed.websocket;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedHashMap;


import com.fed.client.ClientInfo;
import com.fed.client.Clients;
import com.fed.message.Message;
import com.fed.train.model.MultiRegression;
import com.fed.util.ByteBufferUtil;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.nd4j.linalg.api.ndarray.INDArray;


//每一个任务均会创建一个FedWebSocketServer 对象
public class FedWebSocketServer extends WebSocketServer {


    protected final Clients onlineClients=new Clients();//在线用户
    protected int updateWeightNum=0;
    protected ArrayList<ClientInfo> successClients=new ArrayList<ClientInfo>();

    public FedWebSocketServer(int port) throws UnknownHostException {
        super(new InetSocketAddress(port));
        this.start();
        System.out.println("FedSocketServer started on port: " + this.getPort());
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
        clientInfo.setWebSocket(conn);
        onlineClients.clients.add(clientInfo);

        System.out.println(onlineClientCount()+" client(s) now");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println(conn + "WebSocket closed!");

        onlineClients.clients.removeIf(clientInfo -> clientInfo.getWebSocket().equals(conn));

        System.out.println(onlineClientCount()+" client(s) now");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println(conn + ": " + message);
    }

    @lombok.SneakyThrows
    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {
        //第一个更新到达
        MultiRegression.FIRST_UPDATE =true;
        Message tempMessage=(Message) ByteBufferUtil.getObject(message);

        for (int i=0;i<onlineClientCount();i++){
            if(onlineClients.clients.get(i).getWebSocket().equals(conn)){
                ClientInfo clientInfo=onlineClients.clients.get(i);
                clientInfo.setUpdateWeight(tempMessage.weight);
                successClients.add(clientInfo);
                break;
            }
        }

        System.out.println(conn + " update model : \n" + tempMessage.weight);
        updateWeightNum++;
    }



    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
        if (conn != null) {
            // some errors like port binding failed may not be assignable to a specific websocket
        }
    }


    //打印所有参与者信息
    public void printAllClient(){
        for (int i=0;i<onlineClientCount();i++){
            System.out.println(onlineClients.clients.get(i).getClientHandshake());
        }
    }

    //在线人数
    public int onlineClientCount(){
        return onlineClients.clients.size();
    }

    //清空上一轮相关信息
    public void clear(){
/*        for(int i=0;i<onlineClientCount();i++){
            onlineClients.clients.get(i).setUpdateWeight(null);
        }*/
        successClients.clear();
        updateWeightNum=0;
    }

    public ArrayList<WebSocket> getWebSocketSet(){
        ArrayList<WebSocket> webSockets=new ArrayList<WebSocket>();
        for(int i=0;i<onlineClientCount();i++){
            webSockets.add(onlineClients.clients.get(i).getWebSocket());
        }
        return webSockets;
    }


}
