package com.github.adrian99.neuralnetwork.layer;

import com.github.adrian99.neuralnetwork.layer.neuron.OutputLayerNeuron;
import com.github.adrian99.neuralnetwork.layer.neuron.activation.ActivationFunction;
import com.github.adrian99.neuralnetwork.layer.neuron.weightinitialization.WeightInitializationFunction;
import com.github.adrian99.neuralnetwork.learning.LearningFunction;
import com.github.adrian99.neuralnetwork.learning.error.ErrorFunction;

public class OutputNeuronsLayer implements NeuronsLayer {
    private final OutputLayerNeuron[] neurons;

    public OutputNeuronsLayer(int neuronsCount,
                              int inputsCount,
                              ActivationFunction activationFunction,
                              WeightInitializationFunction weightInitializationFunction) {
        neurons = new OutputLayerNeuron[neuronsCount];
        for (var i = 0; i < neuronsCount; i++) {
            neurons[i] = new OutputLayerNeuron(i, inputsCount, activationFunction, weightInitializationFunction);
        }
    }

    public void setPreviousLayer(NeuronsLayer previousLayer) {
        for (var neuron : neurons) {
            neuron.setPreviousLayer(previousLayer);
        }
    }

    @Override
    public OutputLayerNeuron[] getNeurons() {
        return neurons;
    }

    public double[] activate() {
        var outputs = new double[neurons.length];
        for (var i = 0; i < neurons.length; i++) {
            neurons[i].calculateOutput();
            outputs[i] = neurons[i].getOutput();
        }
        return outputs;
    }

    public void calculateNeuronErrors(ErrorFunction errorFunction, int[] targetOutputs) {
        if (neurons.length == targetOutputs.length) {
            for (var i = 0; i < neurons.length; i++) {
                neurons[i].calculateError(errorFunction, targetOutputs[i]);
            }
        } else {
            throw new IllegalArgumentException("Target outputs count mismatch - expected: " + neurons.length + ", received: " + targetOutputs.length);
        }
    }

    public void calculateNewNeuronWeights(LearningFunction learningFunction) {
        for (var neuron : neurons) {
            neuron.calculateNewWeights(learningFunction);
        }
    }
}
