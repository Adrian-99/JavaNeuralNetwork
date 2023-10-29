package com.github.adrian99.neuralnetwork.learning.errorfunction;

public class SumSquaredErrorFunction implements ErrorFunction {
    @Override
    public double apply(double[] networkOutputs, double[] expectedOutputs) {
        if (networkOutputs.length == expectedOutputs.length) {
            var squaresSum = 0.0;
            for (var i = 0; i < networkOutputs.length; i++) {
                squaresSum += Math.pow(networkOutputs[i] - expectedOutputs[i], 2);
            }
            return squaresSum / 2;
        } else {
            throw new IllegalArgumentException("Network outputs and expected outputs lengths differ");
        }
    }

    @Override
    public double applyDerivative(double networkOutput, double targetOutput) {
        return networkOutput - targetOutput;
    }
}
