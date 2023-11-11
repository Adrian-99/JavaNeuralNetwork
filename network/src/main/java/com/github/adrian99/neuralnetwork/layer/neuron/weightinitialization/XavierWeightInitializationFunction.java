package com.github.adrian99.neuralnetwork.layer.neuron.weightinitialization;

import java.util.Random;

public class XavierWeightInitializationFunction implements WeightInitializationFunction {
    private final Random random;
    private double lowerBound;
    private double upperBound;

    public XavierWeightInitializationFunction() {
        random = new Random();
        lowerBound = -1;
        upperBound = 1;
    }

    @Override
    public void calculateBounds(int inputsCount, int neuronsInLayerCount) {
        upperBound = 1 / Math.sqrt(inputsCount);
        lowerBound = -upperBound;
    }

    @Override
    public double getNextValue() {
        return random.nextDouble(lowerBound, upperBound);
    }
}
