package com.fed.train.model;

import android.util.Log;

import com.fed.train.MyMultiLayerNetwork;

import org.deeplearning4j.datasets.iterator.impl.ListDataSetIterator;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;

import java.util.List;
import java.util.Random;

/**
 * 多元线性回归
 * 拟合 y = a x1 + b x2 + c
 */
public class MultiRegression extends MyMultiLayerNetwork {

    private static final String TAG = "MultiRegression";
    
    //随机数种子，用于结果复现
    private static final int seed = 12345;
    //对于每个miniBatch的迭代次数
    private static final int iterations = 10;
    //epoch数量(全部数据的训练次数)
    private static final int nEpochs = 20;
    //一共生成多少样本点
    private static final int nSamples = 1000;
    //Batch size: i.e., each epoch has nSamples/batchSize parameter updates
    private static final int batchSize = 100;
    //网络模型学习率
    private static final double learningRate = 0.01;
    //随机数据生成的范围
    private static int MIN_RANGE = 0;
    private static int MAX_RANGE = 3;

    private static final Random rng = new Random(seed);


    public void execute(){
        Log.i(TAG, "execute: ");

        //Generate the training data
        DataSetIterator iterator = getTrainingData(batchSize,rng);

        //监听器
        model.init();
        model.setListeners(new ScoreIterationListener(10));

        Log.i(TAG, "execute: "+model);

        Log.i(TAG, "execute: "+iterator);
        for( int i=0; i<nEpochs; i++ ){
            iterator.reset();
            model.fit(iterator);
        }

        //测试
/*        final INDArray input = Nd4j.create(new double[] { 0.111111, 0.3333333333333 }, new int[] { 1, 2 });
        INDArray out = model.output(input, false);
        System.out.println(out);*/
        System.out.println(model.gradient().gradient());

    }

    private DataSetIterator getTrainingData(int batchSize, Random rand) {
        double [] sum = new double[nSamples];
        double [] input1 = new double[nSamples];
        double [] input2 = new double[nSamples];
        for (int i= 0; i< nSamples; i++) {
            input1[i] = MIN_RANGE + (MAX_RANGE - MIN_RANGE) * rand.nextDouble();
            input2[i] =  MIN_RANGE + (MAX_RANGE - MIN_RANGE) * rand.nextDouble();
            sum[i] = input1[i] + input2[i];
        }
        INDArray inputNDArray1 = Nd4j.create(input1, new int[]{nSamples,1});
        INDArray inputNDArray2 = Nd4j.create(input2, new int[]{nSamples,1});
        INDArray inputNDArray = Nd4j.hstack(inputNDArray1,inputNDArray2);
        INDArray outPut = Nd4j.create(sum, new int[]{nSamples, 1});
        DataSet dataSet = new DataSet(inputNDArray, outPut);
        List<DataSet> listDs = dataSet.asList();

        return new ListDataSetIterator(listDs,batchSize);
    }

    @Override
    public void updateModel(MultiLayerNetwork globalModel) {
        super.updateModel(globalModel);
    }

    @Override
    public MultiLayerNetwork getModel(){
        return super.getModel();
    }
}
