package com.github.adrian99.neuralnetwork.learning.error;

public class MeanSquaredErrorFunction implements ErrorFunction {
    @Override
    public double apply(double[] networkOutputs, int[] expectedOutputs) {
        if (networkOutputs.length == expectedOutputs.length) {
            var squaresSum = 0.0;
            for (var i = 0; i < networkOutputs.length; i++) {
                squaresSum += Math.pow(networkOutputs[i] - expectedOutputs[i], 2);
            }
            return squaresSum / networkOutputs.length;
        } else {
            throw new IllegalArgumentException("Network outputs and expected outputs lengths differ");
        }
    }

    @Override
    public double applyDerivative(double networkOutput, int targetOutput) {
        return networkOutput - targetOutput;
    }
}
