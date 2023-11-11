package com.github.adrian99.neuralnetwork.layer;

import com.github.adrian99.neuralnetwork.layer.neuron.InputLayerNeuron;
import com.github.adrian99.neuralnetwork.layer.neuron.Neuron;
import com.github.adrian99.neuralnetwork.layer.neuron.activation.ActivationFunction;
import com.github.adrian99.neuralnetwork.layer.neuron.weightinitialization.WeightInitializationFunction;
import com.github.adrian99.neuralnetwork.learning.LearningFunction;

public class InputNeuronsLayer implements NeuronsLayer {
    private final InputLayerNeuron[] neurons;

    public InputNeuronsLayer(int neuronsCount,
                             int inputsCount,
                             ActivationFunction activationFunction,
                             WeightInitializationFunction weightInitializationFunction) {
        neurons = new InputLayerNeuron[neuronsCount];
        for (var i = 0; i < neuronsCount; i++) {
            neurons[i] = new InputLayerNeuron(i, inputsCount, activationFunction, weightInitializationFunction);
        }
    }

    public void setNextLayer(NeuronsLayer nextLayer) {
        for (var neuron : neurons) {
            neuron.setNextLayer(nextLayer);
        }
    }

    @Override
    public Neuron[] getNeurons() {
        return neurons;
    }

    public void activate(double[] inputs) {
        for (var neuron : neurons) {
            neuron.calculateOutput(inputs);
        }
    }

    public void calculateNeuronErrors() {
        for (var neuron : neurons) {
            neuron.calculateError();
        }
    }

    public void calculateNewNeuronWeights(LearningFunction learningFunction, double[] inputs) {
        for (var neuron : neurons) {
            neuron.calculateNewWeights(learningFunction, inputs);
        }
    }
}
