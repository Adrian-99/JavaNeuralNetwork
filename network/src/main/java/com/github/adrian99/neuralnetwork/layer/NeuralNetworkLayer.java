package com.github.adrian99.neuralnetwork.layer;

import com.github.adrian99.neuralnetwork.layer.neuron.Neuron;
import com.github.adrian99.neuralnetwork.layer.neuron.activationfunction.ActivationFunction;

public class NeuralNetworkLayer {
    private final Neuron[] neurons;

    public NeuralNetworkLayer(int neuronsCount, int inputsCount, ActivationFunction activationFunction) {
        neurons = new Neuron[neuronsCount];
        for (var i = 0; i < neuronsCount; i++) {
            neurons[i] = new Neuron(inputsCount, activationFunction);
        }
    }

    public double[] activate(double[] inputs) {
        var result = new double[neurons.length];
        for (var i = 0; i < neurons.length; i++) {
            result[i] = neurons[i].activate(inputs);
        }
        return result;
    }
}
