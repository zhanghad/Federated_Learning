package com.fed;

import com.fed.websocket.WebSocketController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class FedserverApplication {

    public static void main(String[] args) throws IOException, InterruptedException {
        //开启web服务，开启后台web管理界面
        SpringApplication.run(FedserverApplication.class, args);

        //创建实例,开启websocket服务，支持与客户端的交互
        System.out.println("websocket start");
        WebSocketController webSocketController=new WebSocketController();
        webSocketController.startFedTask();

    }

}
