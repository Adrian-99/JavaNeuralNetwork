package com.github.adrian99.neuralnetwork;

import com.github.adrian99.neuralnetwork.layer.*;
import com.github.adrian99.neuralnetwork.layer.neuron.activation.ActivationFunction;
import com.github.adrian99.neuralnetwork.layer.neuron.weightinitialization.WeightInitializationFunction;
import com.github.adrian99.neuralnetwork.learning.LearningFunction;
import com.github.adrian99.neuralnetwork.learning.error.ErrorFunction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static com.github.adrian99.neuralnetwork.util.Utils.toShuffledList;

public abstract class NeuralNetwork implements Serializable {
    public abstract NeuronsLayer[] getLayers();
    public abstract double[] activate(double[] inputs);

    protected abstract void calculateNeuronErrors(ErrorFunction errorFunction, double[] targetOutputs);

    protected abstract void calculateNewNeuronWeights(LearningFunction learningFunction, double[] inputs);

    public void learnSingleEpoch(double[][] inputSets,
                                 double[][] targetOutputSets,
                                 ErrorFunction errorFunction,
                                 LearningFunction learningFunction) {
        if (inputSets.length == targetOutputSets.length) {
            IntStream.range(0, inputSets.length)
                    .boxed()
                    .collect(toShuffledList())
                    .forEach(i -> {
                        activate(inputSets[i]);
                        calculateNeuronErrors(errorFunction, targetOutputSets[i]);
                        calculateNewNeuronWeights(learningFunction, inputSets[i]);
                    });
        } else {
            throw new IllegalArgumentException("Input sets and target output sets counts mismatch: " + inputSets.length + " != " + targetOutputSets.length);
        }
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
