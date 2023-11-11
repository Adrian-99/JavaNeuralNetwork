package com.github.adrian99.neuralnetwork.layer.neuron;

import com.github.adrian99.neuralnetwork.layer.NeuronsLayer;
import com.github.adrian99.neuralnetwork.layer.neuron.activation.ActivationFunction;
import com.github.adrian99.neuralnetwork.layer.neuron.weightinitialization.WeightInitializationFunction;
import com.github.adrian99.neuralnetwork.learning.LearningFunction;
import com.github.adrian99.neuralnetwork.learning.error.ErrorFunction;

public class OutputLayerNeuron extends Neuron {
    private NeuronsLayer previousLayer;

    public OutputLayerNeuron(int index,
                             int inputsCount,
                             ActivationFunction activationFunction,
                             WeightInitializationFunction weightInitializationFunction) {
        super(index, inputsCount, activationFunction, weightInitializationFunction);
    }

    public void setPreviousLayer(NeuronsLayer previousLayer) {
        this.previousLayer = previousLayer;
    }

    public void calculateOutput() {
        super.calculateOutput(previousLayer);
    }

    public void calculateError(ErrorFunction errorFunction, double targetOutput) {
        super.calculateErrorForOutputLayer(errorFunction, targetOutput);
    }

    public void calculateNewWeights(LearningFunction learningFunction) {
        super.calculateNewWeights(learningFunction, previousLayer);
    }
}
