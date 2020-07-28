package com.fedserver.train;

import org.deeplearning4j.nn.gradient.DefaultGradient;
import org.deeplearning4j.nn.gradient.Gradient;


public interface FedModel{

    public void configModel();

    public void evaluateModel();


}
