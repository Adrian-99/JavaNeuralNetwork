package com.github.adrian99.neuralnetwork.layer.neuron;

import com.github.adrian99.neuralnetwork.layer.neuron.activationfunction.ActivationFunction;

import java.util.Arrays;

public class Neuron {
    private final ActivationFunction activationFunction;
    private final double[] weights;
    private double bias;

    public Neuron(int inputsCount, ActivationFunction activationFunction) {
        this.activationFunction = activationFunction;
        this.weights = new double[inputsCount];
        Arrays.fill(weights, 1.0);
        bias = 1.0;
    }

    public double activate(double[] inputs) {
        if (inputs.length == weights.length) {
            var activationValue = bias;
            for (var i = 0; i < weights.length; i++) {
                activationValue += inputs[i] * weights[i];
            }
            return activationFunction.apply(activationValue);
        } else {
            throw new IllegalArgumentException("Neuron inputs count mismatch: expected " + weights.length + ", received " + inputs.length);
        }
    }
}
