package com.github.adrian99.neuralnetwork.layer.neuron.activation;

import java.io.Serializable;

public interface ActivationFunction extends Serializable {
    double apply(double input);
    double applyDerivative(double output);
}
