package com.github.adrian99.neuralnetwork;

import com.github.adrian99.neuralnetwork.layer.*;
import com.github.adrian99.neuralnetwork.layer.neuron.activation.ActivationFunction;
import com.github.adrian99.neuralnetwork.layer.neuron.weightinitialization.WeightInitializationFunction;
import com.github.adrian99.neuralnetwork.learning.LearningFunction;
import com.github.adrian99.neuralnetwork.learning.error.ErrorFunction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class NeuralNetwork implements Serializable {
    public abstract NeuronsLayer[] getLayers();
    public abstract double[] activate(double[] inputs);

    protected abstract void calculateNeuronErrors(ErrorFunction errorFunction, int[] targetOutputs);

    protected abstract void calculateNewNeuronWeights(LearningFunction learningFunction, double[] inputs);

    public void learnSingleEpoch(double[] inputs,
                                 int[] targets,
                                 ErrorFunction errorFunction,
                                 LearningFunction learningFunction) {
        activate(inputs);
        calculateNeuronErrors(errorFunction, targets);
        calculateNewNeuronWeights(learningFunction, inputs);
    }

    public double[][] activate(double[][] inputSets) {
        var results = new double[inputSets.length][];
        for (var i = 0; i < inputSets.length; i++) {
            results[i] = activate(inputSets[i]);
        }
        return results;
    }

    public static class Builder {
        private final int networkOutputsCount;
        private final List<HiddenNeuronsLayer> hiddenLayers;
        private InputNeuronsLayer inputLayer = null;
        private int nextLayerInputsCount;

        public Builder(int networkInputsCount, int networkOutputsCount) {
            this.networkOutputsCount = networkOutputsCount;
            nextLayerInputsCount = networkInputsCount;
            hiddenLayers = new ArrayList<>();
        }

        public Builder addLayer(int neuronsCount,
                                ActivationFunction activationFunction,
                                WeightInitializationFunction weightInitializationFunction) {
            weightInitializationFunction.calculateBounds(nextLayerInputsCount, neuronsCount);
            if (inputLayer != null) {
                hiddenLayers.add(new HiddenNeuronsLayer(
                        neuronsCount,
                        nextLayerInputsCount,
                        activationFunction,
                        weightInitializationFunction
                ));
            } else {
                inputLayer = new InputNeuronsLayer(
                        neuronsCount,
                        nextLayerInputsCount,
                        activationFunction,
                        weightInitializationFunction
                );
            }
            nextLayerInputsCount = neuronsCount;
            return this;
        }

        public NeuralNetwork addOutputLayer(ActivationFunction activationFunction,
                                            WeightInitializationFunction weightInitializationFunction) {
            weightInitializationFunction.calculateBounds(nextLayerInputsCount, networkOutputsCount);
            if (inputLayer != null) {
                var outputLayer = new OutputNeuronsLayer(
                        networkOutputsCount,
                        nextLayerInputsCount,
                        activationFunction,
                        weightInitializationFunction
                );
                return new MultiLayerNeuralNetwork(inputLayer, outputLayer, hiddenLayers.toArray(HiddenNeuronsLayer[]::new));
            } else {
                var layer = new SingleNeuronsLayer(
                        networkOutputsCount,
                        nextLayerInputsCount,
                        activationFunction,
                        weightInitializationFunction
                );
                return new SingleLayerNeuralNetwork(layer);
            }
        }
    }
}
