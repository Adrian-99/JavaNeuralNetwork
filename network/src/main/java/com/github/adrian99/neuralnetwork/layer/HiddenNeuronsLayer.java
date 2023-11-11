package com.github.adrian99.neuralnetwork.layer;

import com.github.adrian99.neuralnetwork.layer.neuron.HiddenLayerNeuron;
import com.github.adrian99.neuralnetwork.layer.neuron.Neuron;
import com.github.adrian99.neuralnetwork.layer.neuron.activation.ActivationFunction;
import com.github.adrian99.neuralnetwork.layer.neuron.weightinitialization.WeightInitializationFunction;
import com.github.adrian99.neuralnetwork.learning.LearningFunction;

public class HiddenNeuronsLayer implements NeuronsLayer {
    private final HiddenLayerNeuron[] neurons;

    public HiddenNeuronsLayer(int neuronsCount,
                              int inputsCount,
                              ActivationFunction activationFunction,
                              WeightInitializationFunction weightInitializationFunction) {
        neurons = new HiddenLayerNeuron[neuronsCount];
        for (var i = 0; i < neuronsCount; i++) {
            neurons[i] = new HiddenLayerNeuron(i, inputsCount, activationFunction, weightInitializationFunction);
        }
    }

    public void setSurroundingLayers(NeuronsLayer previousLayer, NeuronsLayer nextLayer) {
        for (var neuron : neurons) {
            neuron.setSurroundingLayers(previousLayer, nextLayer);
        }
    }

    @Override
    public Neuron[] getNeurons() {
        return neurons;
    }

    public void activate() {
        for (var neuron : neurons) {
            neuron.calculateOutput();
        }
    }

    public void calculateNeuronErrors() {
        for (var neuron : neurons) {
            neuron.calculateError();
        }
    }

    public void calculateNewNeuronWeights(LearningFunction learningFunction) {
        for (var neuron : neurons) {
            neuron.calculateNewWeights(learningFunction);
        }
    }
}
