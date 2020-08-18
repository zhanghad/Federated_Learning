package com.fed.websocket;

import com.fed.client.ClientInfo;
import com.fed.message.Constant;
import com.fed.message.Message;
import com.fed.util.ByteBufferUtil;
import com.fed.train.model.MultiRegression;
import com.fed.util.SaveAndLoad;
import org.java_websocket.WebSocket;

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class WebSocketController {

    public static int taskCount;


    public int ROUND = 2;
    public int PORT = 8887;
    public int MIN_UPDATE = 1;//需满足的最小更新数
    public int MIN_CLIENTS = 1;
    public int MAX_CLIENTS = 2;
    public int STAGE_REPORT_TIME = 10;//秒


    private final MultiRegression fedTask=new MultiRegression();//学习任务实例
    private final FedWebSocketServer fedWebSocketServer=new FedWebSocketServer(PORT);//fedWebSocketServer 实例
    private final Message message=new Message();
    private final ArrayList<WebSocket> selectedConn=new ArrayList<WebSocket>();
    private ArrayList<ClientInfo> selectedClients;



    public WebSocketController() throws UnknownHostException {
        taskCount++;
    }

    public void startFedTask() throws IOException, InterruptedException {

        System.out.println(fedTask);

        //进行 ROUND 轮通信
        for (int i = 0; i < ROUND; i++) {
            System.out.println("\nround :" + i + " start");
            //prepare阶段
            prepareStage(i);
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
        fedTask.evalGlobalModel();

        //保存模型
        SaveAndLoad.saveMultiLayerNetwork(fedTask.getGlobalModel(),MultiRegression.SAVE_PATH,true);

        taskCount--;
    }

    private void prepareStage(int i) throws InterruptedException, IOException {


        //等待足够数量的clients
        while (fedWebSocketServer.onlineClientCount() < MIN_CLIENTS) {
            System.out.println("正在等待的用户数量:" + fedWebSocketServer.onlineClientCount());
            try {
                System.out.println("参与用户数量过少, 等待...");
                Thread.sleep(5 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.print("等待其他用户");
        for (int j = 0; j < 5; j++) {
            System.out.print(".");
            Thread.sleep(1000);
        }
        System.out.println();


        //选择加入的客户
        System.out.println("选择参与用户...");
        selectedClients = fedWebSocketServer.onlineClients.selectClients(MAX_CLIENTS);
        for (ClientInfo selectedClient : selectedClients) {
            selectedConn.add(selectedClient.getWebSocket());
        }


        System.out.println("select stage finish.");
        //发送配置信息：数据结构如何设计？ 待设计

        //发送全局模型
        System.out.println("向参与用户发送全局模型...");
        System.out.println(fedTask);


/*        if (fedTask == null) {
            System.out.println("fedTask == null");
            fedTask = new MultiRegression();
        }*/

        if(i==0){
            message.setMessage(Constant.SERVER,Constant.SERVER_GLOBAL_MODEL,fedTask.getGlobalModel(),null);
            System.out.println(message.order);
            System.out.println(message.owner);
            System.out.println(message.model);
            System.out.println(message.model.paramTable());
        }else {
            System.out.println("message.setMessage");
            message.setMessage(Constant.SERVER,Constant.SERVER_GLOBAL_WEIGHT,null,fedTask.getGlobalWeight());
        }
        ByteBuffer byteBuffer = ByteBufferUtil.getByteBuffer(message);
        System.out.println(byteBuffer);
        fedWebSocketServer.broadcast(byteBuffer, selectedConn);

        //通知所有已加入用户开始训练
        System.out.println("all client start");
        fedWebSocketServer.broadcast(Constant.ALL_CLIENT_START, selectedConn);

        System.out.println("config stage finish.");

    }

    private boolean reportStage() throws InterruptedException {
        int well_update=0;

        //第一个更新到达
        while (!MultiRegression.FIRST_UPDATE) ;

        //等待时间限制
        for (int i = 0; i < STAGE_REPORT_TIME; i++) {
            System.out.print(".");
            Thread.sleep(1000);
        }
        System.out.println("\n");



        //检查模型评分



        //如果本轮中未达到最小更新梯度数量，则训练失败
        if (fedWebSocketServer.updateWeightNum < MIN_UPDATE) {
            //通知所有加入的client停止服务
            System.out.println("本轮返回的更新模型过少，训练失败");
            fedWebSocketServer.broadcast(Constant.ALL_CLIENT_STOP, selectedConn);
            return false;
        }



        //若达到最小限制
        //聚合所有更新梯度
        for (int i=0;i<fedWebSocketServer.successClients.size();i++){
            if(!fedTask.updateGlobalWeight(fedWebSocketServer.successClients.get(i).getUpdateWeight())){
                System.out.println("更新全局模型出错");
                break;
            }
        }
        fedTask.updateGlobalModel();


        System.out.println("聚合完后全局模型的权重\n" + fedTask.getGlobalWeight());

        //清空更新模型，保护隐私
        fedWebSocketServer.clear();

        //展示本轮参与的用户
        System.out.println("本轮参与的用户有：");
        fedWebSocketServer.printAllClient();
        System.out.println();

        //通知所有加入的用户本轮训练结束
        fedWebSocketServer.broadcast(Constant.ALL_CLIENT_STOP, selectedConn);

        MultiRegression.FIRST_UPDATE = false;

        return true;
    }


}
