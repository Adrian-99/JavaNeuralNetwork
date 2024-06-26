package com.github.adrian99.neuralnetwork.layer.neuron.activation;

public class LinearActivationFunction implements ActivationFunction {
    private final double slope;
    private final double intercept;

    public LinearActivationFunction(double slope, double intercept) {
        this.slope = slope;
        this.intercept = intercept;
    }

    public LinearActivationFunction(double slope) {
        this.slope = slope;
        this.intercept = 0.0;
    }

    public double getSlope() {
        return slope;
    }

    public double getIntercept() {
        return intercept;
    }

    @Override
    public double apply(double input) {
        return slope * input + intercept;
    }

    @Override
    public double applyDerivative(double output) {
        return slope;
    }
}
