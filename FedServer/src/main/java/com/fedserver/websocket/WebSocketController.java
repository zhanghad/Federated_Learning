package com.fedserver.websocket;

import com.fedserver.Util.ByteBufferUtil;
import com.fedserver.train.model.MultiRegression;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.java_websocket.WebSocket;

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketController {

    public static volatile boolean FIRST_UPDATE = false;
    public final String START_CODE = "start_MultiRegression";
    public final String STOP_CODE = "stop_MultiRegression";
    public int ROUND = 2;
    public int PORT = 8887;
    public int MIN_UPDATE = 1;//需满足的最小更新数
    public int MIN_CLIENTS = 1;
    public int MAX_CLIENTS = 2;
    public int STAGE_REPORT_TIME = 10;//秒

    private Stage stage = Stage.SELECT;//所处阶段
    private MultiRegression fedTask;//学习任务实例
    private ConcurrentHashMap<WebSocket, ClientInfo> joinedClients;//加入训练任务的用户
    private FedWebSocketServer fedWebSocketServer;//fedWebSocketServer 实例


    public WebSocketController() throws UnknownHostException {
        joinedClients = new ConcurrentHashMap<WebSocket, ClientInfo>();
        //实例化任务（此处可设置web接口控制，选择不同的模型）
        fedTask = new MultiRegression();
        fedTask.configModel();
        //开启websocket服务（PORT后改为动态调整的，同时进行多项任务）
        fedWebSocketServer = new FedWebSocketServer(PORT);
    }

    public void startFedWebsocketServer() throws IOException, InterruptedException {

        System.out.println(fedTask);

        //进行 ROUND 轮通信
        for (int i = 0; i < ROUND; i++) {
            System.out.println("\nround :" + i + " start");
            //prepare阶段
            prepareStage();
            //report阶段
            if (!reportStage()) {
                //训练失败
                System.out.println("round :" + i + " failed");
                i--;
                continue;
            }
            System.out.println("round :" + i + " finish\n");
        }

        System.out.println("训练成功");

        System.out.println("全局梯度为");
        System.out.println(fedTask.getGlobalWeight());

        System.out.println("*********************************************************************************");


        //测试全局模型
        fedTask.evaluateModel();

    }

    private void prepareStage() throws InterruptedException, IOException {

        stage = Stage.SELECT;

        //等待足够数量的clients
        while (fedWebSocketServer.getOnlineClientCount() < MIN_CLIENTS) {
            System.out.println("正在等待的用户数量:" + fedWebSocketServer.getOnlineClientCount());
            try {
                System.out.println("参与用户数量过少, 等待...");
                Thread.sleep(5 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.print("等待其他用户");
        for (int i = 0; i < 5; i++) {
            System.out.print(".");
            Thread.sleep(1000);
        }
        System.out.println();


        //选择加入的客户
        System.out.println("选择参与用户...");
        joinedClients = selectClients();

        stage = Stage.CONFIG;
        System.out.println("select stage finish.");
        //发送配置信息：数据结构如何设计？ 待设计

        //发送全局模型
        System.out.println("向参与用户发送全局模型...");
        System.out.println(fedTask);


        if (fedTask == null) {
            System.out.println("fedTask == null");
            fedTask = new MultiRegression();
            fedTask.configModel();
        }

        ByteBuffer byteBuffer = ByteBufferUtil.getByteBuffer(fedTask.getGlobalModel());

        fedWebSocketServer.broadcast(byteBuffer, joinedClients.keySet());

        //通知所有已加入用户开始训练
        System.out.println("all client " + START_CODE);
        fedWebSocketServer.broadcast(START_CODE, joinedClients.keySet());

        stage = Stage.REPORT;
        System.out.println("config stage finish.");

    }

    //report 阶段
    private boolean reportStage() throws InterruptedException {

        //第一个更新到达
        while (!FIRST_UPDATE) ;

        //等待时间限制
        for (int i = 0; i < STAGE_REPORT_TIME; i++) {
            System.out.print(".");
            Thread.sleep(1000);
        }
        System.out.println("\n");


        //如果本轮中未达到最小更新梯度数量，则训练失败
        if (fedWebSocketServer.getUpdateModelNum() < MIN_UPDATE) {
            //通知所有加入的client停止服务
            System.out.println("本轮返回的更新模型过少，训练失败");
            fedWebSocketServer.broadcast("stop_MultiRegression", joinedClients.keySet());
            return false;
        }


        //若达到最小限制
        //聚合所有更新梯度
        Iterator<MultiLayerNetwork> it = fedWebSocketServer.getUpdateModel().iterator();
        while (it.hasNext()) {
            if (!fedTask.updateGlobalWeight(it.next().paramTable())) {
                System.out.println("更新全局模型出错");
                break;
            }
        }

        System.out.println("聚合完后全局模型的权重\n" + fedTask.getGlobalWeight());

        //清空更新模型，保护隐私
        fedWebSocketServer.clearUpdateModel();

        //展示本轮参与的用户
        System.out.println("本轮参与的用户有：");
        fedWebSocketServer.printAllClient();
        System.out.println();

        //通知所有加入的用户本轮训练结束
        fedWebSocketServer.broadcast(STOP_CODE, joinedClients.keySet());

        FIRST_UPDATE = false;

        return true;
    }


    //选择本轮加入的用户
    private ConcurrentHashMap<WebSocket, ClientInfo> selectClients() {

        if (fedWebSocketServer.getOnlineClientCount() <= MAX_CLIENTS) {
            return fedWebSocketServer.getWebSocketSet();
        } else {
            //评价
            //排序
            //选择MAX数量的Client
            return fedWebSocketServer.getWebSocketSet();
        }

    }

    //评价算法
    //待设计
    private int evaluateClient(ClientInfo clientInfo) {
        int evaluation = 0;

        return evaluation;
    }

    public enum Stage {
        SELECT, CONFIG, REPORT
    }


}
