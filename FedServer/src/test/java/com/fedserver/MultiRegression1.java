package com.fedserver;

import com.fedserver.train.model.MultiRegression;
import org.deeplearning4j.datasets.iterator.impl.ListDataSetIterator;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.gradient.DefaultGradient;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 多元线性回归
 * 拟合 y = a x1 + b x2 + c
 */
public class MultiRegression1 {
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

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        //Generate the training data
        DataSetIterator iterator = getTrainingData(batchSize,rng);

        //Create the network
        int numInput = 2;
        int numOutputs = 1;
        MultiLayerNetwork net = new MultiLayerNetwork(new NeuralNetConfiguration.Builder()
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
        net.init();


//        net.setListeners(new ScoreIterationListener(1));

        net.fit(iterator);

/*        System.out.println(net.gradient());
        System.out.println(net.gradient().gradient());*/
        //System.out.println(net1.gradient().gradient()+" ");


        //INDArray array=Nd4j.ones(1,3);
/*        INDArray array1=Nd4j.zeros(1,3);
        INDArray array2=Nd4j.ones(1,3);

        System.out.println("array: "+array);
        System.out.println("array1: "+array1);
        array1=array1.add(array).div(2);
        array2=array2.addi(array);
        System.out.println("array: "+array);
        System.out.println("array1: "+array1);
        System.out.println("array2: "+array2);

        System.out.println("************************************************************");*/


//        Map<String,INDArray> para=net.paramTable();

        System.out.println(net.gradient());

        //转为流
        ByteBuffer byteBuffer=getByteBuffer(net.gradient());
        System.out.println(byteBuffer);
        //转为对象
        Object gradient=getObject(byteBuffer);
        System.out.println(gradient);

        System.out.println(gradient.getClass());
        System.out.println(((DefaultGradient) gradient).gradient());

        net.update((DefaultGradient)gradient);
        System.out.println(net.gradient().getClass());

        Map<String,INDArray> para=((DefaultGradient) gradient).gradientForVariable();
        System.out.println(para);
        System.out.println(net.gradient());


/*        MultiRegression fedTask=new MultiRegression();
        fedTask.configModel();
        fedTask.updateGlobalGradient((DefaultGradient) net.gradient());
        System.out.println(fedTask.getGlobalGradient());*/




        final INDArray input = Nd4j.create(new double[] { 0.111111, 0.3333333333333 }, new int[] { 1, 2 });
        INDArray out = net.output(input, false);
/*        System.out.println(out);
        System.out.println(net.gradient());*/





    }

    private static DataSetIterator getTrainingData(int batchSize, Random rand) {
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




    public static byte[] getBytes(Serializable obj) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bout);
        out.writeObject(obj);
        out.flush();
        byte[] bytes = bout.toByteArray();
        bout.close();
        out.close();
        return bytes;
    }


    public static Object getObject(ByteBuffer byteBuffer) throws ClassNotFoundException, IOException {
        InputStream input = new ByteArrayInputStream(byteBuffer.array());
        ObjectInputStream oi = new ObjectInputStream(input);
        Object obj = oi.readObject();
        input.close();
        oi.close();
        byteBuffer.clear();
        return obj;
    }


    public static ByteBuffer getByteBuffer(Serializable obj) throws IOException {
        byte[] bytes = getBytes(obj);
        ByteBuffer buff = ByteBuffer.wrap(bytes);
        return buff;
    }




}
