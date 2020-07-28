package com.fedserver.train.model;

import com.fedserver.train.Aggregator;
import com.fedserver.train.FedModel;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.gradient.DefaultGradient;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.util.Random;

public class MultiRegression extends Aggregator implements FedModel {

    /*超参数*/
    //随机数种子，用于结果复现
    private final int seed = 12345;
    //对于每个miniBatch的迭代次数
    private final int iterations = 10;
    //epoch数量(全部数据的训练次数)
    private final int nEpochs = 20;
    //一共生成多少样本点
    private final int nSamples = 1000;
    //Batch size: i.e., each epoch has nSamples/batchSize parameter updates
    private final int batchSize = 100;
    //网络模型学习率
    private final double learningRate = 0.01;
    private final Random rng = new Random(seed);
    //随机数据生成的范围
    private int MIN_RANGE = 0;
    private int MAX_RANGE = 3;


    @Override
    public void configModel() {
        //创建模型
        int numInput = 2;
        int numOutputs = 1;
        globalModel = new MultiLayerNetwork(new NeuralNetConfiguration.Builder()
                .seed(seed)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .weightInit(WeightInit.XAVIER)
                .updater(new Sgd(learningRate))
                .list()
                .layer(0, new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                        .activation(Activation.IDENTITY)
                        .nIn(numInput).nOut(numOutputs).build())
                .build()
        );
        globalModel.init();
    }

    @Override
    public void evaluateModel() {
        //测试数据
        final INDArray input = Nd4j.create(new double[] { 0.111111, 0.3333333333333 }, new int[] { 1, 2 });
        INDArray out = globalModel.output(input, false);
        System.out.println("GlobalModel test result: "+out);
        System.out.println(globalModel.gradient());
    }

    @Override
    public boolean updateGlobalGradient(DefaultGradient updateGradient) {
        return super.updateGlobalGradient(updateGradient);
    }

    @Override
    public MultiLayerNetwork getGlobalModel() {
        return super.getGlobalModel();
    }

    @Override
    public DefaultGradient getGlobalGradient() {
        return super.getGlobalGradient();
    }



}
