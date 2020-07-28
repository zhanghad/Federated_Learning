package com.fedserver.websocket;


import org.java_websocket.handshake.ClientHandshake;

//用户信息
//待设计
public class ClientInfo {

    private ClientHandshake clientHandshake;

    public void setClientHandshake(ClientHandshake clientHandshake) {
        this.clientHandshake = clientHandshake;
    }

    public ClientHandshake getClientHandshake() {
        return clientHandshake;
    }
}
