package com.fed.util;

import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;

import java.io.File;
import java.io.IOException;

public class SaveAndLoad {
    //负责保存和加载模型的工具类


    public static void saveMultiLayerNetwork(MultiLayerNetwork model,String path) throws IOException {
        //path instance : "model/MyMultiLayerNetwork.zip"
        File locationToSave = new File(path);
        boolean saveUpdater = true;
        ModelSerializer.writeModel(model, locationToSave, saveUpdater);

    }

    public static MultiLayerNetwork loadMultiLayerNetwork(String path) throws IOException {
        //Load the model
        return ModelSerializer.restoreMultiLayerNetwork(path);
    }

    public static void saveComputationGraph(ComputationGraph model,String path) throws IOException {
        //path instance: "model/MyComputationGraph.zip"
        File locationToSave = new File(path);
        boolean saveUpdater = true;
        ModelSerializer.writeModel(model, locationToSave, saveUpdater);
    }

    public static ComputationGraph loadComputationGraph(String path) throws IOException {
        //Load the model
        return ModelSerializer.restoreComputationGraph(path);
    }



}
