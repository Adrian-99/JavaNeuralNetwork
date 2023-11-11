package com.github.adrian99.neuralnetwork;

import com.github.adrian99.neuralnetwork.layer.SingleNeuronsLayer;
import com.github.adrian99.neuralnetwork.learning.LearningFunction;
import com.github.adrian99.neuralnetwork.learning.error.ErrorFunction;

public class SingleLayerNeuralNetwork extends NeuralNetwork {
    private final SingleNeuronsLayer layer;

    public SingleLayerNeuralNetwork(SingleNeuronsLayer layer) {
        this.layer = layer;
    }

    @Override
    public double[] activate(double[] inputs) {
        return layer.activate(inputs);
    }

    @Override
    protected void calculateNeuronErrors(ErrorFunction errorFunction, double[] targetOutputs) {
        layer.calculateNeuronErrors(errorFunction, targetOutputs);
    }

    @Override
    protected void calculateNewNeuronWeights(LearningFunction learningFunction, double[] inputs) {
        layer.calculateNewNeuronWeights(learningFunction, inputs);
    }
}
