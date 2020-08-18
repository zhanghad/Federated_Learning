package com.fed.train;


import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

//聚合算法
public class Aggregator {

    protected MultiLayerNetwork globalModel;//全局模型
    protected Map<String,INDArray> tempWeight;

    public MultiLayerNetwork getGlobalModel() {
        return globalModel;
    }

    public LinkedHashMap<String,INDArray> getGlobalWeight(){
        return (LinkedHashMap<String,INDArray>)globalModel.paramTable();
    }

    public boolean updateGlobalWeight(Map<String,INDArray> updateWeight){
        if(tempWeight==null){
            tempWeight=updateWeight;
        }else {
            int layerNum = globalModel.paramTable().size() / 2;

            if (updateWeight.size()==tempWeight.size()) {
                for (int i = 0; i < layerNum; i++) {
                    //判断每一层的形状是否相同
                    if (Arrays.equals(tempWeight.get(i + "_W").shape(), updateWeight.get(i + "_W").shape()) &&
                            Arrays.equals(tempWeight.get(i + "_b").shape(), updateWeight.get(i + "_b").shape())) {
                        INDArray avgWeights = tempWeight.get(i + "_W").add(updateWeight.get(i + "_W")).div(2);
                        INDArray avgBias =tempWeight.get(i + "_b").add(updateWeight.get(i + "_b")).div(2);
                        tempWeight.replace(i + "_W",avgWeights);
                        tempWeight.replace(i + "_b",avgBias);

                    } else {
                        System.out.println("第" + i + "层网络形状不一致");
                        return false;
                    }
                }
            } else {
                System.out.println("网络层数不一致");
                return false;
            }
        }

        return true;
    }

    public void updateGlobalModel(){
        //更新全局参数权重
        if(tempWeight==null){
            System.out.println("tempWeight is null");
        }else {
            globalModel.setParamTable(tempWeight);
        }
    }



}
