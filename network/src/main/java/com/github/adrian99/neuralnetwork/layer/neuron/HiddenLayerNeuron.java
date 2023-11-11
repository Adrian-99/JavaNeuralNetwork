package com.github.adrian99.neuralnetwork.layer.neuron;

import com.github.adrian99.neuralnetwork.layer.NeuronsLayer;
import com.github.adrian99.neuralnetwork.layer.neuron.activation.ActivationFunction;
import com.github.adrian99.neuralnetwork.layer.neuron.weightinitialization.WeightInitializationFunction;
import com.github.adrian99.neuralnetwork.learning.LearningFunction;

public class HiddenLayerNeuron extends Neuron {
    private NeuronsLayer previousLayer;
    private NeuronsLayer nextLayer;

    public HiddenLayerNeuron(int index,
                             int inputsCount,
                             ActivationFunction activationFunction,
                             WeightInitializationFunction weightInitializationFunction) {
        super(index, inputsCount, activationFunction, weightInitializationFunction);
    }

    public void setSurroundingLayers(NeuronsLayer previousLayer, NeuronsLayer nextLayer) {
        this.previousLayer = previousLayer;
        this.nextLayer = nextLayer;
    }

    public void calculateOutput() {
        super.calculateOutput(previousLayer);
    }

    public void calculateError() {
        super.calculateError(nextLayer);
    }

    public void calculateNewWeights(LearningFunction learningFunction) {
        super.calculateNewWeights(learningFunction, previousLayer);
    }
}
