package com.github.adrian99.neuralnetwork.learning.error;

public interface ErrorFunction {
    double apply(double[] networkOutputs, int[] expectedOutputs);
    double applyDerivative(double networkOutput, int targetOutput);
}
