package com.github.adrian99.neuralnetwork.learning.data;

import com.github.adrian99.neuralnetwork.NeuralNetwork;
import com.github.adrian99.neuralnetwork.learning.error.ErrorFunction;

public abstract class DataProvider {
    protected final double[][] inputs;
    protected final int[][] targets;

    protected double accuracy = 0;
    protected double error = Double.MAX_VALUE;

    protected DataProvider(double[][] inputs, int[][] targets) {
        if (inputs.length == targets.length) {
            this.inputs = inputs;
            this.targets = targets;
        } else {
            throw new IllegalArgumentException("Inputs and targets must be the same length");
        }
    }

    public double getAccuracy() {
        return accuracy;
    }

    public double getError() {
        return error;
    }

    public abstract InputsAndTargets getLearningData();
    public abstract void performValidation(NeuralNetwork neuralNetwork, ErrorFunction errorFunction);
}
