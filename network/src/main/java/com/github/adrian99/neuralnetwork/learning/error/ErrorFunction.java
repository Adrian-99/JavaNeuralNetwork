package com.github.adrian99.neuralnetwork.learning.error;

public interface ErrorFunction {
    double apply(double[] networkOutputs, double[] expectedOutputs);
    double applyDerivative(double networkOutput, double targetOutput);
}
