package com.github.adrian99.neuralnetwork.learning.data;

import com.github.adrian99.neuralnetwork.NeuralNetwork;
import com.github.adrian99.neuralnetwork.learning.error.ErrorFunction;

public abstract class DataProvider {
    protected final double[][] inputs;
    protected final int[][] targets;

    protected DataProvider(double[][] inputs, int[][] targets) {
        if (inputs.length == targets.length) {
            this.inputs = inputs;
            this.targets = targets;
        } else {
            throw new IllegalArgumentException("Inputs and targets must be the same length");
        }
    }

    public abstract LearningAndValidationData getData();
    public abstract void update(NeuralNetwork neuralNetwork, ErrorFunction errorFunction);
    public abstract double getAccuracy();
    public abstract double getError();
}
