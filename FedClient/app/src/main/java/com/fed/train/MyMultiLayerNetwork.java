package com.fed.train;


import android.util.Log;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.LinkedHashMap;


public class MyMultiLayerNetwork {
    private static final String TAG = "MyMultiLayerNetwork";

    //模型
    protected MultiLayerNetwork model;
    //protected MultiLayerNetwork globalModel;

    public boolean RECEIVED_GLOBAL;


    public void updateModel(MultiLayerNetwork globalModel){
        Log.i(TAG, "updateModel: ");
        model=globalModel;
    }

    public void updateModel(LinkedHashMap<String,INDArray> weight){
        Log.i(TAG, "updateModel: ");
        model.setParamTable(weight);
    }


    public MultiLayerNetwork getModel(){
        return model;
    }

    public LinkedHashMap<String, INDArray>getWeight(){
        return (LinkedHashMap<String, INDArray>)model.paramTable();
    }

}
