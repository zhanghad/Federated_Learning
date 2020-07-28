package com.fedserver.train;

import org.deeplearning4j.nn.gradient.DefaultGradient;
import org.deeplearning4j.nn.gradient.Gradient;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.Arrays;
import java.util.Map;

//聚合算法
public class Aggregator {

    protected MultiLayerNetwork globalModel;//全局模型
    protected DefaultGradient globalGradient;//全局梯度

    public MultiLayerNetwork getGlobalModel() {
        return globalModel;
    }

    public DefaultGradient getGlobalGradient() {
        return globalGradient;
    }


    public boolean updateGlobalGradient(DefaultGradient updateGradient) {

        if (globalGradient == null) {
            globalGradient=updateGradient;
            return true;
        }

        Map<String, INDArray> globalPara = globalGradient.gradientForVariable();
        Map<String, INDArray> updatePara = updateGradient.gradientForVariable();
        int layerNum = globalGradient.gradientForVariable().size() / 2;
        INDArray avgWeight;
        INDArray avgBias;

        if (updatePara.size() == globalPara.size()) {
            for (int i = 0; i < layerNum; i++) {
                //判断每一层的形状是否相同
                if (Arrays.equals(globalPara.get(i + "_W").shape(), updatePara.get(i + "_W").shape()) &&
                        Arrays.equals(globalPara.get(i + "_b").shape(), updatePara.get(i + "_b").shape())) {

                    avgWeight = globalPara.get(i + "_W").add(updatePara.get(i + "_W")).div(2);
                    avgBias = globalPara.get(i + "_b").add(updatePara.get(i + "_b")).div(2);

                    globalGradient.setGradientFor(i + "_W", avgWeight);
                    globalGradient.setGradientFor(i + "_b", avgBias);

                } else {
                    System.out.println("第" + i + "层网络形状不一致");
                    return false;
                }
            }
        } else {
            System.out.println("网络层数不一致");
            return false;
        }
        return true;
    }


}
