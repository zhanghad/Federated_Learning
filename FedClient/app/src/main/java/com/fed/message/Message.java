package com.fed.message;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.io.Serializable;
import java.util.LinkedHashMap;

public class Message implements Serializable {
    private static final long serialVersionUID=20000L;

    public byte owner;
    public byte order;

    public MultiLayerNetwork model;

    public LinkedHashMap<String, INDArray> weight;

    public void setMessage(byte owner,byte order,MultiLayerNetwork model,LinkedHashMap<String, INDArray> weight){
        this.weight=weight;
        this.model=model;
        this.order=order;
        this.owner=owner;
    }
}
