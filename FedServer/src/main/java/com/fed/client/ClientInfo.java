package com.fed.client;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.LinkedHashMap;

public class ClientInfo {

    private String deviceId;//用户id
    private WebSocket webSocket;
    private ClientHandshake clientHandshake;
    private double income;

    public LinkedHashMap<String, INDArray> updateWeight;
    public Reputation clientReputation=new Reputation();//用户声誉
    public double cpuFreq;//CPU频率
    //...其他设备信息




    public double getDeviceScore(){
        //设备得分评价
        //...
        return 0;
    }



    public double getClientSocre(){
        //用户评分计算
        //评分依据[声誉评分 && 设备评分]
        //...


        return 0;
    }

    //参与者奖励计算
    public double getReword(double modelScore){
        double reward=getClientSocre()*modelScore;
        income+=reward;
        return reward;
    }



    public void setClientHandshake(ClientHandshake clientHandshake) {
        this.clientHandshake = clientHandshake;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setWebSocket(WebSocket webSocket) {
        this.webSocket = webSocket;
    }

    public void setUpdateWeight(LinkedHashMap<String, INDArray> updateWeight) {
        this.updateWeight = updateWeight;
    }

    public ClientHandshake getClientHandshake() {
        return clientHandshake;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public WebSocket getWebSocket() {
        return webSocket;
    }

    public LinkedHashMap<String, INDArray> getUpdateWeight() {
        return updateWeight;
    }
}
