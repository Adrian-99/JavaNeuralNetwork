package com.github.adrian99.neuralnetwork.layer.neuron.activationfunction;

public class LogisticActivationFunction implements ActivationFunction {
    private final double supremum;
    private final double growthRate;

    public LogisticActivationFunction(double growthRate, double supremum) {
        this.supremum = supremum;
        this.growthRate = growthRate;
    }

    public LogisticActivationFunction(double growthRate) {
        this.supremum = 1.0;
        this.growthRate = growthRate;
    }

    @Override
    public double apply(double input) {
        return supremum / (1 + Math.exp(-growthRate * input));
    }
}
