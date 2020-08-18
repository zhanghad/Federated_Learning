package com.fed;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.fed.websocket.WebSocketService;
import com.fedclient.R;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int UPDATE = 1;
    private static final String TAG = "MainActivity";
    public static final String getUrl = "http://192.168.1.3:8080/fedserver/hello";
    public static final String postUrl = "http://192.168.1.3:8080/fedserver/model";


    private Handler handler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case UPDATE:

                    break;
                default:
                    break;
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn_get = findViewById(R.id.btn1);
        Button btn_startService = findViewById(R.id.btn_startservice);
        Button btn_stopService = findViewById(R.id.btn_stopservice);


        btn_get.setOnClickListener(this);
        btn_startService.setOnClickListener(this);
        btn_stopService.setOnClickListener(this);


        Log.d(TAG, "onCreate: ");
    }

    @Override
    public void onClick(View view) {

        Intent webSocketIntent=new Intent(MainActivity.this, WebSocketService.class);

        switch (view.getId()) {
            case R.id.btn1:
/*                //http post测试
                INDArray test = Nd4j.zeros(1, 3);
                try {
                    HttpUtil.httpRequestPost(postUrl, Nd4j.toByteArray(test), new okhttp3.Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            Log.d(TAG, "onFailure: ");
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            Log.d(TAG, "onResponse: " + response.body().string());
                        }
                    });
                } catch (IOException e) {
                    Log.e(TAG, "onClick: R.id.btn1");
                    e.printStackTrace();
                }*/

                Log.d(TAG, "onClick: btn1");
                break;
            case R.id.btn_startservice:
                Log.i(TAG, "onClick: join");
                //与服务器连接，加入联邦学习
                startService(webSocketIntent);
                break;
            case R.id.btn_stopservice:
                Log.i(TAG, "onClick: close");
                //断开webSocket连接，退出联邦学习
                stopService(webSocketIntent);
                Log.d(TAG, "onClick: ");
                break;
            default:
                break;
        }
    }

}
