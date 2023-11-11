package com.github.adrian99.neuralnetwork.learning;

public class BackPropagationLearningFunction implements LearningFunction {
    private final double learningRate;

    public BackPropagationLearningFunction(double learningRate) {
        this.learningRate = learningRate;
    }

    public double calculateNewWeight(double currentWeight, double neuronError, double input) {
        return currentWeight - (learningRate * neuronError * input);
    }

    public double calculateNewBias(double currentBias, double neuronError) {
        return calculateNewWeight(currentBias, neuronError, 1);
    }
}
