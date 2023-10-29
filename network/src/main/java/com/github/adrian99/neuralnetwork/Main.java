package com.github.adrian99.neuralnetwork;

import com.github.adrian99.neuralnetwork.layer.neuron.activationfunction.LinearActivationFunction;
import com.github.adrian99.neuralnetwork.layer.neuron.activationfunction.LogisticActivationFunction;
import com.github.adrian99.neuralnetwork.layer.neuron.weightinitializationfunction.NormalizedXavierWeightInitializationFunction;
import com.github.adrian99.neuralnetwork.learning.errorfunction.ErrorFunction;
import com.github.adrian99.neuralnetwork.learning.errorfunction.SumSquaredErrorFunction;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
//        var input = new double[] { 15.7543, 4347.5345, 1.6747, -35.45, 0.7658 };
//
//        var activationFunction = new LinearActivationFunction(2.0);
//        var network = new NeuralNetwork.Builder(5, 2)
//                .addLayer(5, activationFunction)
//                .addLayer(7, activationFunction)
//                .addFinalLayer(activationFunction);
//
//        System.out.println(Arrays.toString(input));
//        System.out.println(Arrays.toString(network.activate(input)));

//        var inputs = new double[100][2];
//        var target = new double[100][1];
//
//        for (var i = 0; i < 10; i++) {
//            for (var j = 0; j < 10; j++) {
//                var d1 = 0.5 * (i + 1);
//                var d2 = 0.25 * (j + 1);
//                inputs[i * 10 + j] = new double[] { d1, d2 };
//                target[i * 10 + j] = new double[] { d1 + d2 };
//            }
//        }

//        System.out.println("INPUTS:");
//        printArray2D(inputs);
//        System.out.println("TARGET:");
//        printArray2D(target);

        var inputs = new double[][] {
                new double[] { 0, 0 },
                new double[] { 0, 1 },
                new double[] { 1, 0 },
                new double[] { 1, 1 }
        };
        var target = new double[][] {
                new double[] { 0 },
                new double[] { 1 },
                new double[] { 1 },
                new double[] { 0 }
        };

        var activationFunction = new LogisticActivationFunction(5);
        var weightInitializationFunction = new NormalizedXavierWeightInitializationFunction();
        var errorFunction = new SumSquaredErrorFunction();
        var network = new NeuralNetwork.Builder(2, 1)
                .addLayer(4, activationFunction, weightInitializationFunction)
                .addFinalLayer(activationFunction, weightInitializationFunction);

        var outputs = network.activate(inputs);
        System.out.println("ERR: " + calculateTotalError(errorFunction, outputs, target));

        for (var i = 0; i < 1000000; i++) {
            network.learnSingleEpoch(inputs, target, errorFunction, 20000.0);
        }

        outputs = network.activate(inputs);
        System.out.println("ERR: " + calculateTotalError(errorFunction, outputs, target));
        printArray2D(outputs);
    }

    private static void printArray2D(double[][] array) {
        System.out.println("[");
        for (var arrayPart : array) {
            System.out.println("\t" + Arrays.toString(arrayPart) + ",");
        }
        System.out.println("]");
    }

    private static double calculateTotalError(ErrorFunction errorFunction, double[][] outputs, double[][] targetOutputs) {
        var result = 0.0;
        for (var i = 0; i < outputs.length; i++) {
            result += errorFunction.apply(outputs[i], targetOutputs[i]);
        }
        return result;
    }
}
