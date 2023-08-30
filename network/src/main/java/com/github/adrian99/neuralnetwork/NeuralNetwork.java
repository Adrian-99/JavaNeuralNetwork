package com.github.adrian99.neuralnetwork;

import com.github.adrian99.neuralnetwork.layer.NeuralNetworkLayer;
import com.github.adrian99.neuralnetwork.layer.neuron.activationfunction.ActivationFunction;

import java.util.ArrayList;
import java.util.List;

public class NeuralNetwork {
    private final NeuralNetworkLayer[] layers;

    public NeuralNetwork(NeuralNetworkLayer... layers) {
        this.layers = layers;
    }

    public double[] activate(double[] inputs) {
        var results = inputs;
        for (var layer : layers) {
            results = layer.activate(results);
        }
        return results;
    }

    public static class Builder {
        private final int networkOutputsCount;
        private int nextLayerInputsCount;
        private List<NeuralNetworkLayer> layers;

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
