package com.github.adrian99.neuralnetwork.layer.neuron.weightinitialization;

import java.util.Random;

public class NormalizedXavierWeightInitializationFunction implements WeightInitializationFunction {
    private final Random random;
    private double lowerBound;
    private double upperBound;

    public NormalizedXavierWeightInitializationFunction() {
        random = new Random();
        lowerBound = -1;
        upperBound = 1;
    }

    @Override
    public void calculateBounds(int inputsCount, int neuronsInLayerCount) {
        upperBound = Math.sqrt(6) / Math.sqrt(inputsCount + neuronsInLayerCount);
        lowerBound = -upperBound;
    }

    @Override
    public double getNextValue() {
        return random.nextDouble(lowerBound, upperBound);
    }
}
