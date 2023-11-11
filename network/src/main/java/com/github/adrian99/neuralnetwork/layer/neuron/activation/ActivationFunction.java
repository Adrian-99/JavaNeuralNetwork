package com.github.adrian99.neuralnetwork.layer.neuron.activation;

public interface ActivationFunction {
    double apply(double input);
    double applyDerivative(double output);
}
