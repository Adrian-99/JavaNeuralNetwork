package com.github.adrian99.neuralnetwork.layer;

import com.github.adrian99.neuralnetwork.layer.neuron.Neuron;
import com.github.adrian99.neuralnetwork.layer.neuron.activationfunction.ActivationFunction;
import com.github.adrian99.neuralnetwork.learning.errorfunction.ErrorFunction;

public class NeuralNetworkLayer {
    private final Neuron[] neurons;
    private boolean hasPreviousLayer;
    private boolean hasNextLayer;

    public NeuralNetworkLayer(int neuronsCount, int inputsCount, ActivationFunction activationFunction) {
        neurons = new Neuron[neuronsCount];
        for (var i = 0; i < neuronsCount; i++) {
            neurons[i] = new Neuron(i, inputsCount, activationFunction);
        }
    }

    public double[] activate(double[] inputs) {
        var result = new double[neurons.length];
        for (var i = 0; i < neurons.length; i++) {
            result[i] = neurons[i].calculateOutput(inputs);
        }
        return result;
    }

    public void calculateNeuronErrors(ErrorFunction errorFunction, double[] targetOutputs) {
        if (hasNextLayer) {
            for (var neuron : neurons) {
                neuron.calculateError();
            }
        } else {
            if (neurons.length == targetOutputs.length) {
                for (var i = 0; i < neurons.length; i++) {
                    neurons[i].calculateError(errorFunction, targetOutputs[i]);
                }
            } else {
                throw new IllegalArgumentException("Target outputs count mismatch - expected: " + neurons.length + ", received: " + targetOutputs.length);
            }
        }
    }

    public void calculateNewNeuronWeights(double learningRate, double[] inputs) {
        for (var neuron : neurons) {
            neuron.calculateNewWeights(learningRate, inputs);
        }
    }

    public void setPreviousLayer(NeuralNetworkLayer layer) {
        hasPreviousLayer = layer != null;
        for (var neuron : neurons) {
            neuron.setPreviousLayer(layer);
        }
    }

    public void setNextLayer(NeuralNetworkLayer layer) {
        hasNextLayer = layer != null;
        for (var neuron : neurons) {
            neuron.setNextLayer(layer);
        }
    }

    public Neuron[] getNeurons() {
        return neurons;
    }
}
