package com.github.adrian99.neuralnetwork.layer;

import com.github.adrian99.neuralnetwork.layer.neuron.Neuron;
import com.github.adrian99.neuralnetwork.layer.neuron.SingleLayerNeuron;
import com.github.adrian99.neuralnetwork.layer.neuron.activation.ActivationFunction;
import com.github.adrian99.neuralnetwork.layer.neuron.weightinitialization.WeightInitializationFunction;
import com.github.adrian99.neuralnetwork.learning.LearningFunction;
import com.github.adrian99.neuralnetwork.learning.error.ErrorFunction;

public class SingleNeuronsLayer implements NeuronsLayer {
    private final SingleLayerNeuron[] neurons;

    public SingleNeuronsLayer(int neuronsCount,
                              int inputsCount,
                              ActivationFunction activationFunction,
                              WeightInitializationFunction weightInitializationFunction) {
        neurons = new SingleLayerNeuron[neuronsCount];
        for (var i = 0; i < neuronsCount; i++) {
            neurons[i] = new SingleLayerNeuron(i, inputsCount, activationFunction, weightInitializationFunction);
        }
    }

    @Override
    public Neuron[] getNeurons() {
        return neurons;
    }

    public double[] activate(double[] inputs) {
        var outputs = new double[neurons.length];
        for (var i = 0; i < neurons.length; i++) {
            neurons[i].calculateOutput(inputs);
            outputs[i] = neurons[i].getOutput();
        }
        return outputs;
    }

    public void calculateNeuronErrors(ErrorFunction errorFunction, double[] targetOutputs) {
        if (neurons.length == targetOutputs.length) {
            for (var i = 0; i < neurons.length; i++) {
                neurons[i].calculateError(errorFunction, targetOutputs[i]);
            }
        } else {
            throw new IllegalArgumentException("Target outputs count mismatch - expected: " + neurons.length + ", received: " + targetOutputs.length);
        }
    }

    public void calculateNewNeuronWeights(LearningFunction learningFunction, double[] inputs) {
        for (var neuron : neurons) {
            neuron.calculateNewWeights(learningFunction, inputs);
        }
    }
}
