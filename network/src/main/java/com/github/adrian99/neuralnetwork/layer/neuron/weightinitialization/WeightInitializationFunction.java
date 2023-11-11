package com.github.adrian99.neuralnetwork.layer.neuron.weightinitialization;

public interface WeightInitializationFunction {
    void calculateBounds(int inputsCount, int neuronsInLayerCount);
    double getNextValue();
}
