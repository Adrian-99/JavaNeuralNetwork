package com.github.adrian99.neuralnetwork.layer.neuron;

import com.github.adrian99.neuralnetwork.layer.NeuronsLayer;
import com.github.adrian99.neuralnetwork.layer.neuron.activation.ActivationFunction;
import com.github.adrian99.neuralnetwork.layer.neuron.weightinitialization.WeightInitializationFunction;
import com.github.adrian99.neuralnetwork.learning.LearningFunction;

public class InputLayerNeuron extends Neuron {
    private NeuronsLayer nextLayer;

    public InputLayerNeuron(int index,
                            int inputsCount,
                            ActivationFunction activationFunction,
                            WeightInitializationFunction weightInitializationFunction) {
        super(index, inputsCount, activationFunction, weightInitializationFunction);
    }

    public void setNextLayer(NeuronsLayer nextLayer) {
        this.nextLayer = nextLayer;
    }

    public void calculateOutput(double[] inputs) {
        super.calculateOutputForInputLayer(inputs);
    }

    public void calculateError() {
        super.calculateError(nextLayer);
    }

    public void calculateNewWeights(LearningFunction learningFunction, double[] inputs) {
        super.calculateNewWeightsForInputLayer(learningFunction, inputs);
    }
}
