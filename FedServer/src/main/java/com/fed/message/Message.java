package com.fed.message;

import com.fed.util.ByteBufferUtil;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
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

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Message message=new Message();
        ByteBuffer byteBuffer= ByteBufferUtil.getByteBuffer(message);
        System.out.println(byteBuffer);
        Object object=ByteBufferUtil.getObject(byteBuffer);
        System.out.println(object.getClass());
        System.out.println(((Message)object).model);
    }

}
