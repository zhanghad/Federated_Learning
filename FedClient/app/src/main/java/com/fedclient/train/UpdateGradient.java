package com.fedclient.train;

import org.deeplearning4j.nn.gradient.DefaultGradient;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;

public class UpdateGradient {

    //梯度
    protected DefaultGradient localGradient;
    //模型
    protected MultiLayerNetwork model;
    public boolean RECEIVED_GLOBAL;


    public void updateGradient(DefaultGradient globalGradient){
        localGradient=globalGradient;
    }

    public void updateModel(){
        model.update(localGradient);
    }

    public MultiLayerNetwork getModel(){
        return model;
    }

}
