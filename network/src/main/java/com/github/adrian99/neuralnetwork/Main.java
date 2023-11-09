package com.github.adrian99.neuralnetwork;

import com.github.adrian99.neuralnetwork.data.csv.CsvDataLoader;
import com.github.adrian99.neuralnetwork.layer.neuron.activationfunction.LinearActivationFunction;
import com.github.adrian99.neuralnetwork.layer.neuron.activationfunction.LogisticActivationFunction;
import com.github.adrian99.neuralnetwork.layer.neuron.weightinitializationfunction.NormalizedXavierWeightInitializationFunction;
import com.github.adrian99.neuralnetwork.learning.errorfunction.ErrorFunction;
import com.github.adrian99.neuralnetwork.learning.errorfunction.SumSquaredErrorFunction;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws URISyntaxException, IOException {
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

        var numericData = new CsvDataLoader(new File(Main.class.getClassLoader().getResource("datasets/iris/iris.data").toURI()))
                .mapColumn(4, Map.of("Iris-setosa", "1", "Iris-versicolor", "2", "Iris-virginica", "3"))
                .toNumericData()
                .normalize(0)
                .normalize(1)
                .normalize(2)
                .normalize(3);
        var inputs = numericData.toArray(0, 3);
        var target = numericData.toArray(4, 4);

        var activationFunction = new LogisticActivationFunction(5);
        var weightInitializationFunction = new NormalizedXavierWeightInitializationFunction();
        var errorFunction = new SumSquaredErrorFunction();
        var network = new NeuralNetwork.Builder(4, 1)
//                .addLayer(4, activationFunction, weightInitializationFunction)
                .addLayer(2, activationFunction, weightInitializationFunction)
                .addFinalLayer(new LinearActivationFunction(0.5), weightInitializationFunction);
//                .addFinalLayer(activationFunction, weightInitializationFunction);

        var outputs = network.activate(inputs);
        var error = calculateTotalError(errorFunction, outputs, target);
        System.out.println("ERR: " + error);

        while (error > 1) {
//        while (true) {
            for (var i = 0; i < 10; i++) {
                network.learnSingleEpoch(inputs, target, errorFunction, 1.5);
            }
            outputs = network.activate(inputs);
            error = calculateTotalError(errorFunction, outputs, target);
            System.out.println("ERR: " + error);
        }


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
