package com.github.adrian99.neuralnetwork.learning.data;

public class InputsAndTargets {
    private final double[][] inputs;
    private final int[][] targets;

    public InputsAndTargets(double[][] inputs, int[][] targets) {
        if (inputs.length == targets.length) {
            this.inputs = inputs;
            this.targets = targets;
        } else {
            throw new IllegalArgumentException("Inputs and targets had different lengths");
        }
    }

    public double[][] getInputs() {
        return inputs;
    }

    public int[][] getTargets() {
        return targets;
    }
}
