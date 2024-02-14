package com.github.adrian99.neuralnetwork.layer.neuron;

import com.github.adrian99.neuralnetwork.layer.NeuronsLayer;
import com.github.adrian99.neuralnetwork.layer.neuron.activation.ActivationFunction;
import com.github.adrian99.neuralnetwork.layer.neuron.weightinitialization.WeightInitializationFunction;
import com.github.adrian99.neuralnetwork.learning.LearningFunction;
import com.github.adrian99.neuralnetwork.learning.error.ErrorFunction;

import java.io.Serializable;

public abstract class Neuron implements Serializable {
    protected final int index;
    protected final ActivationFunction activationFunction;
    protected final double[] weights;
    protected double bias;
    protected double output;
    protected double error;

    protected Neuron(int index,
                     int inputsCount,
                     ActivationFunction activationFunction,
                     WeightInitializationFunction weightInitializationFunction) {
        this.index = index;
        this.activationFunction = activationFunction;
        this.weights = new double[inputsCount];
        for (var i = 0; i < inputsCount; i++) {
            weights[i] = weightInitializationFunction.getNextValue();
        }
        bias = weightInitializationFunction.getNextValue();
    }

    public double[] getWeights() {
        return weights;
    }

    public double getBias() {
        return bias;
    }

    public double getOutput() {
        return output;
    }

    protected void calculateOutputForInputLayer(double[] inputs) {
        if (inputs.length == weights.length) {
            var activationValue = bias;
            for (var i = 0; i < weights.length; i++) {
                activationValue += inputs[i] * weights[i];
            }
            output = activationFunction.apply(activationValue);
        } else {
            throw new IllegalArgumentException("Neuron inputs count mismatch: expected " + weights.length + ", received " + inputs.length);
        }
    }

    protected void calculateOutput(NeuronsLayer previousLayer) {
        var activationValue = bias;
        for (var i = 0; i < weights.length; i++) {
            activationValue += previousLayer.getNeurons()[i].output * weights[i];
        }
        output = activationFunction.apply(activationValue);
    }

    protected void calculateErrorForOutputLayer(ErrorFunction errorFunction, double targetOutput) {
        error = errorFunction.applyDerivative(output, targetOutput) * activationFunction.applyDerivative(output);
    }

    protected void calculateError(NeuronsLayer nextLayer) {
        error = 0;
        for (var neuron : nextLayer.getNeurons()) {
            error += neuron.error * neuron.weights[index];
        }
        error *= activationFunction.applyDerivative(output);
    }

    protected void calculateNewWeights(LearningFunction learningFunction, NeuronsLayer previousLayer) {
        for (var previousNeuron : previousLayer.getNeurons()) {
            weights[previousNeuron.index] = learningFunction.calculateNewWeight(
                    weights[previousNeuron.index],
                    error,
                    previousNeuron.output
            );
        }
        bias = learningFunction.calculateNewBias(bias, error);
    }

    protected void calculateNewWeightsForInputLayer(LearningFunction learningFunction, double[] inputs) {
        if (inputs.length == weights.length) {
            for (var i = 0; i < weights.length; i++) {
                weights[i] = learningFunction.calculateNewWeight(weights[i], error, inputs[i]);
            }
            bias = learningFunction.calculateNewBias(bias, error);
        } else {
            throw new IllegalArgumentException("Neuron inputs count mismatch: expected " + weights.length + ", received " + inputs.length);
        }
    }
}
