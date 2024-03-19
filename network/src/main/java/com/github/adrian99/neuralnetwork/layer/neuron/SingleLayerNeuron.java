package com.github.adrian99.neuralnetwork.layer.neuron;

import com.github.adrian99.neuralnetwork.layer.neuron.activation.ActivationFunction;
import com.github.adrian99.neuralnetwork.layer.neuron.weightinitialization.WeightInitializationFunction;
import com.github.adrian99.neuralnetwork.learning.LearningFunction;
import com.github.adrian99.neuralnetwork.learning.error.ErrorFunction;

public class SingleLayerNeuron extends Neuron {
    public SingleLayerNeuron(int index,
                             int inputsCount,
                             ActivationFunction activationFunction,
                             WeightInitializationFunction weightInitializationFunction) {
        super(index, inputsCount, activationFunction, weightInitializationFunction);
    }

    public void calculateOutput(double[] inputs) {
        super.calculateOutputForInputLayer(inputs);
    }

    public void calculateError(ErrorFunction errorFunction, int targetOutput) {
        super.calculateErrorForOutputLayer(errorFunction, targetOutput);
    }

    public void calculateNewWeights(LearningFunction learningFunction, double[] inputs) {
        super.calculateNewWeightsForInputLayer(learningFunction, inputs);
    }
}
