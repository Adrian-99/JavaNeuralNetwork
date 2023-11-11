package com.github.adrian99.neuralnetwork.learning;

public interface LearningFunction {
    double calculateNewWeight(double currentWeight, double neuronError, double input);
    double calculateNewBias(double currentBias, double neuronError);
}
