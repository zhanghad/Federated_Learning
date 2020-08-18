package com.fed.train;

import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.HashMap;

public interface Evaluate {

    public double evalGlobalModel();

    public double evalModel(HashMap<String, INDArray> weight);

}
