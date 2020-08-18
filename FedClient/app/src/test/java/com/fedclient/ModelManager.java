package com.fedclient;

import android.util.Log;

import com.fed.train.model.MultiRegression;

public class ModelManager {

    public static final String MULTIREGRESSION_START="start_MultiRegression";
    public static final String MULTIREGRESSION_STOP="stop_MultiRegression";

    public static final String TAG = "ModelManager";
    private static MultiRegression multiRegression;


    public static void executeModel(String code){

        switch (code){
            case MULTIREGRESSION_START:{
                //开始训练
                multiRegression=new MultiRegression();
                multiRegression.execute();
                break;
            }
            case MULTIREGRESSION_STOP:{
                if(multiRegression!=null)
                    multiRegression=null;
                break;
            }
            //可以添加其他模型

            default:
                Log.e(TAG, "execute: wrong code" );
                break;
        }

    }

}
