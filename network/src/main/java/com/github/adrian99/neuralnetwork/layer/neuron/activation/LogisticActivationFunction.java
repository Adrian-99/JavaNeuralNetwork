package com.github.adrian99.neuralnetwork.layer.neuron.activation;

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

    public double getGrowthRate() {
        return growthRate;
    }

    public double getSupremum() {
        return supremum;
    }

    @Override
    public double apply(double input) {
        return supremum / (1 + Math.exp(-growthRate * input));
    }

    @Override
    public double applyDerivative(double output) {
        return growthRate * output * (1 - output);
    }
}
