package com.github.adrian99.neuralnetwork.layer.neuron.weightinitializationfunction;

public interface WeightInitializationFunction {
    void calculateBounds(int inputsCount, int neuronsInLayerCount);
    double getNextValue();
}
