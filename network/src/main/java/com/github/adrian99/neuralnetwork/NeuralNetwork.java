package com.github.adrian99.neuralnetwork;

import com.github.adrian99.neuralnetwork.layer.NeuralNetworkLayer;
import com.github.adrian99.neuralnetwork.layer.neuron.activationfunction.ActivationFunction;
import com.github.adrian99.neuralnetwork.learning.errorfunction.ErrorFunction;

import java.util.ArrayList;
import java.util.List;

public class NeuralNetwork {
    private final NeuralNetworkLayer[] layers;

    public NeuralNetwork(NeuralNetworkLayer... layers) {
        this.layers = layers;
        if (layers.length > 1) {
            layers[0].setPreviousLayer(null);
            layers[0].setNextLayer(layers[1]);
            layers[layers.length - 1].setPreviousLayer(layers[layers.length - 2]);
            layers[layers.length - 1].setNextLayer(null);
            for (var i = 1; i < layers.length - 1; i++) {
                layers[i].setPreviousLayer(layers[i - 1]);
                layers[i].setNextLayer(layers[i + 1]);
            }
        }
    }

    public void learnSingleEpoch(double[][] inputSets,
                                 double[][] targetOutputSets,
                                 ErrorFunction errorFunction,
                                 double learningRate) {
        if (inputSets.length == targetOutputSets.length) {
            for (var i = 0; i < inputSets.length; i++) {
                activate(inputSets[i]);
                calculateNeuronErrors(errorFunction, targetOutputSets[i]);
                calculateNewNeuronWeights(learningRate, inputSets[i]);
            }
        } else {
            throw new IllegalArgumentException("Input sets and target output sets counts mismatch: " + inputSets.length + " != " + targetOutputSets.length);
        }
    }

    public double[] activate(double[] inputs) {
        var results = inputs;
        for (var layer : layers) {
            results = layer.activate(results);
        }
        return results;
    }

    public double[][] activate(double[][] inputSets) {
        var results = new double[inputSets.length][];
        for (var i = 0; i < inputSets.length; i++) {
            results[i] = activate(inputSets[i]);
        }
        return results;
    }

    public void calculateNeuronErrors(ErrorFunction errorFunction, double[] targetOutputs) {
        for (var i = layers.length - 1; i >= 0; i--) {
            layers[i].calculateNeuronErrors(errorFunction, targetOutputs);
        }
    }

    public void calculateNewNeuronWeights(double learningRate, double[] inputs) {
        for (var layer : layers) {
            layer.calculateNewNeuronWeights(learningRate, inputs);
        }
    }

    public static class Builder {
        private final int networkOutputsCount;
        private final List<NeuralNetworkLayer> layers;
        private int nextLayerInputsCount;

        public Builder(int networkInputsCount, int networkOutputsCount) {
            this.networkOutputsCount = networkOutputsCount;
            nextLayerInputsCount = networkInputsCount;
            layers = new ArrayList<>();
        }

        public Builder addLayer(int neuronsCount, ActivationFunction activationFunction) {
            layers.add(new NeuralNetworkLayer(neuronsCount, nextLayerInputsCount, activationFunction));
            nextLayerInputsCount = neuronsCount;
            return this;
        }

        public NeuralNetwork addFinalLayer(ActivationFunction activationFunction) {
            addLayer(networkOutputsCount, activationFunction);
            return new NeuralNetwork(layers.toArray(NeuralNetworkLayer[]::new));
        }
    }
}
