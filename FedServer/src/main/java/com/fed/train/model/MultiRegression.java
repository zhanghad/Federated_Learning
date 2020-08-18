package com.fed.train.model;

import com.fed.train.Aggregator;
import com.fed.train.Evaluate;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.util.HashMap;
import java.util.Random;

public class MultiRegression extends Aggregator implements Evaluate {


    public static final String SAVE_PATH="model/MultiRegression.zip";
    public static boolean FIRST_UPDATE = false;

//超参数

    public static int seed = 12345;
    public static int iterations = 10;
    public static int nEpochs = 20;
    public static int nSamples = 1000;
    public static int batchSize = 100;
    public static double learningRate = 0.01;
    private final Random rng = new Random(seed);
    public static int MIN_RANGE = 0;
    public static int MAX_RANGE = 3;

    public MultiRegression(){
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

        //UI界面
        UIServer uiServer = UIServer.getInstance();
        StatsStorage statsStorage = new InMemoryStatsStorage();
        uiServer.attach(statsStorage);
        globalModel.setListeners(new StatsListener(statsStorage));

        globalModel.init();
    }


    @Override
    public double evalGlobalModel() {
        //测试数据
        final INDArray input = Nd4j.create(new double[] { 0.111111, 0.3333333333333 }, new int[] { 1, 2 });
        INDArray out = globalModel.output(input, false);
        System.out.println("GlobalModel test result: "+out);
        System.out.println(globalModel.gradient());
        return 1.0;
    }

    @Override
    public double evalModel(HashMap<String, INDArray> weight) {
        return 0;
    }
}
