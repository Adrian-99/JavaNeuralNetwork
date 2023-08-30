package com.github.adrian99.neuralnetwork;

import com.github.adrian99.neuralnetwork.layer.neuron.activationfunction.LinearActivationFunction;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        var input = new double[] { 15.7543, 4347.5345, 1.6747, -35.45, 0.7658 };

        var activationFunction = new LinearActivationFunction(2.0);
        var network = new NeuralNetwork.Builder(5, 2)
                .addLayer(5, activationFunction)
                .addLayer(7, activationFunction)
                .addFinalLayer(activationFunction);

        System.out.println(Arrays.toString(input));
        System.out.println(Arrays.toString(network.activate(input)));
    }
}
