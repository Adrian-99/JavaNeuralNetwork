package com.github.adrian99.neuralnetwork.layer.neuron.activationfunction;

public interface ActivationFunction {
    double apply(double input);
    double applyDerivative(double output);
}
