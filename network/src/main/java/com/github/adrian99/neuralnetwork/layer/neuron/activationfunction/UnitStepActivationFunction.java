package com.github.adrian99.neuralnetwork.layer.neuron.activationfunction;

public class UnitStepActivationFunction implements ActivationFunction {
    @Override
    public double apply(double input) {
        if (input >= 0) {
            return 1.0;
        } else {
            return 0.0;
        }
    }
}
