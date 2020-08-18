package com.fed.train.model;

import com.fed.train.Aggregator;
import com.fed.train.Evaluate;
import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.learning.config.Nadam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.IOException;
import java.util.HashMap;

public class Mnist extends Aggregator implements Evaluate {

    public static final String SAVE_PATH="model/MultiRegression.zip";
    public static boolean FIRST_UPDATE = false;


    //number of rows and columns in the input pictures
    public static final int numRows = 28;
    public static final int numColumns = 28;
    public static  int outputNum = 10; // number of output classes
    public static  int batchSize = 64; // batch size for each epoch
    public static  int rngSeed = 123; // random number seed for reproducibility
    public static  int numEpochs = 15; // number of epochs to perform
    public static  double rate = 0.0015; // learning rate

    public static double MIN_SCORE=0.8;//模型最低评分

    private DataSetIterator mnistTest = new MnistDataSetIterator(batchSize, false, rngSeed);
    private MultiLayerConfiguration conf;


    public Mnist() throws IOException {

        conf = new NeuralNetConfiguration.Builder()
                .seed(rngSeed) //include a random seed for reproducibility
                .activation(Activation.RELU)
                .weightInit(WeightInit.XAVIER)
                .updater(new Nadam())
                .l2(rate * 0.005) // regularize learning model
                .list()
                .layer(new DenseLayer.Builder() //create the first input layer.
                        .nIn(numRows * numColumns)
                        .nOut(500)
                        .build())
                .layer(new DenseLayer.Builder() //create the second input layer
                        .nIn(500)
                        .nOut(100)
                        .build())
                .layer(new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD) //create hidden layer
                        .activation(Activation.SOFTMAX)
                        .nOut(outputNum)
                        .build())
                .build();

        globalModel = new MultiLayerNetwork(conf);
        globalModel.init();

    }



    @Override
    public double evalGlobalModel() {
        //accuracy,Precision,Recall
        //正确率，精度，召回率？
        Evaluation eval = globalModel.evaluate(mnistTest);
        return eval.accuracy();
    }


    @Override
    public double evalModel(HashMap<String, INDArray> weight) {
        MultiLayerNetwork tempModel=new MultiLayerNetwork(conf);
        tempModel.init();
        Evaluation eval = tempModel.evaluate(mnistTest);
        return eval.accuracy();
    }


}
