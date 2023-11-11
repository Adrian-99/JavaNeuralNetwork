package com.github.adrian99.neuralnetwork.layer.neuron.activation;

public class UnitStepActivationFunction implements ActivationFunction {
    @Override
    public double apply(double input) {
        if (input >= 0) {
            return 1.0;
        } else {
            return 0.0;
        }
    }

    @Override
    public double applyDerivative(double output) {
        return output;
    }
}
