package com.fedclient.train;


import android.util.Log;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;


public class MyMultiLayerNetwork {
    private static final String TAG = "MyMultiLayerNetwork";

    //模型
    protected MultiLayerNetwork model;
    protected MultiLayerNetwork globalModel;

    public boolean RECEIVED_GLOBAL;

/*    public void updateModel(Map<String , INDArray> globalWeight){
        model.setParamTable(globalWeight);
    }*/

    public void updateModel(MultiLayerNetwork globalModel){
        Log.i(TAG, "updateModel: ");
        model=globalModel;
        Log.i(TAG, "updateModel: "+model);
    }


    public MultiLayerNetwork getModel(){
        return model;
    }

}
