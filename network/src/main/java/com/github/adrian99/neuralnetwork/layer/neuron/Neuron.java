package com.github.adrian99.neuralnetwork.layer.neuron;

import com.github.adrian99.neuralnetwork.layer.NeuralNetworkLayer;
import com.github.adrian99.neuralnetwork.layer.neuron.activation.ActivationFunction;
import com.github.adrian99.neuralnetwork.layer.neuron.weightinitialization.WeightInitializationFunction;
import com.github.adrian99.neuralnetwork.learning.LearningFunction;
import com.github.adrian99.neuralnetwork.learning.error.ErrorFunction;

public class Neuron {
    private final int index;
    private final ActivationFunction activationFunction;
    private final double[] weights;
    private double bias;
    private double output;
    private double error;
    private NeuralNetworkLayer previousLayer;
    private NeuralNetworkLayer nextLayer;

    public Neuron(int index,
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

    public void setPreviousLayer(NeuralNetworkLayer layer) {
        previousLayer = layer;
    }

    public void setNextLayer(NeuralNetworkLayer layer) {
        nextLayer = layer;
    }

    public double calculateOutput(double[] inputs) {
        if (inputs.length == weights.length) {
            var activationValue = bias;
            for (var i = 0; i < weights.length; i++) {
                activationValue += inputs[i] * weights[i];
            }
            output = activationFunction.apply(activationValue);
            return output;
        } else {
            throw new IllegalArgumentException("Neuron inputs count mismatch: expected " + weights.length + ", received " + inputs.length);
        }
    }

    public double calculateOutput() {
        var activationValue = bias;
        for (var i = 0; i < weights.length; i++) {
            activationValue += previousLayer.getNeurons()[i].output * weights[i];
        }
        output = activationFunction.apply(activationValue);
        return output;
    }

    public void calculateError(ErrorFunction errorFunction, double targetOutput) {
        if (nextLayer == null) {
            error = errorFunction.applyDerivative(output, targetOutput) * activationFunction.applyDerivative(output);
        } else {
            calculateError();
        }
    }

    public void calculateError() {
        error = 0;
        for (var neuron : nextLayer.getNeurons()) {
            error += neuron.error * neuron.weights[index];
        }
        error *= activationFunction.applyDerivative(output);
    }

    public void calculateNewWeights(LearningFunction learningFunction) {
        for (var previousNeuron : previousLayer.getNeurons()) {
            weights[previousNeuron.index] = learningFunction.calculateNewWeight(
                    weights[previousNeuron.index],
                    error,
                    previousNeuron.output
            );
        }
        bias = learningFunction.calculateNewBias(bias, error);
    }

    public void calculateNewWeights(LearningFunction learningFunction, double[] inputs) {
        if (previousLayer == null) {
            if (inputs.length == weights.length) {
                for (var i = 0; i < weights.length; i++) {
                    weights[i] = learningFunction.calculateNewWeight(weights[i], error, inputs[i]);
                }
                bias = learningFunction.calculateNewBias(bias, error);
            } else {
                throw new IllegalArgumentException("Neuron inputs count mismatch: expected " + weights.length + ", received " + inputs.length);
            }
        } else {
            calculateNewWeights(learningFunction);
        }
    }
}
