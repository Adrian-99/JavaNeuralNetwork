package com.github.adrian99.neuralnetwork;

import com.github.adrian99.neuralnetwork.data.csv.CsvDataLoader;
import com.github.adrian99.neuralnetwork.layer.neuron.activation.LinearActivationFunction;
import com.github.adrian99.neuralnetwork.layer.neuron.activation.LogisticActivationFunction;
import com.github.adrian99.neuralnetwork.layer.neuron.weightinitialization.NormalizedXavierWeightInitializationFunction;
import com.github.adrian99.neuralnetwork.learning.BackPropagationLearningFunction;
import com.github.adrian99.neuralnetwork.learning.data.CrossValidationDataProvider;
import com.github.adrian99.neuralnetwork.learning.data.SimpleDataProvider;
import com.github.adrian99.neuralnetwork.learning.endcondition.AccuracyEndCondition;
import com.github.adrian99.neuralnetwork.learning.endcondition.EpochsCountEndCondition;
import com.github.adrian99.neuralnetwork.learning.endcondition.ErrorEndCondition;
import com.github.adrian99.neuralnetwork.learning.endcondition.TimeEndCondition;
import com.github.adrian99.neuralnetwork.learning.error.ErrorFunction;
import com.github.adrian99.neuralnetwork.learning.error.SumSquaredErrorFunction;
import com.github.adrian99.neuralnetwork.learning.supervisor.LearningStatisticsProvider;
import com.github.adrian99.neuralnetwork.learning.supervisor.LearningSupervisor;
import com.github.adrian99.neuralnetwork.util.Statistics;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Main {
    @SuppressWarnings("java:S106")
    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException, ExecutionException {
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
        var inputs = numericData.toDoubleArray(0, 3);
        var target = numericData.toIntArray(4, 4);

        var weightInitializationFunction = new NormalizedXavierWeightInitializationFunction();
        var network = new NeuralNetwork.Builder(4, 1)
                .addLayer(2, new LogisticActivationFunction(5), weightInitializationFunction)
                .addOutputLayer(new LinearActivationFunction(0.5), weightInitializationFunction);

        var errorFunction = new SumSquaredErrorFunction();
        var learningFunction = new BackPropagationLearningFunction(1.5);

//        var dataProvider = new CrossValidationDataProvider(inputs, target, 10);
        var dataProvider = new SimpleDataProvider(inputs, target);
        var learningSupervisor = new LearningSupervisor(network, dataProvider);

        var stats = learningSupervisor.startLearningAsync(
                new LearningSupervisor.Configuration(errorFunction, learningFunction)
//                        .addEndCondition(new TimeEndCondition(20))
//                        .addEndCondition(new AccuracyEndCondition(0.9999))
                        .addEndCondition(new ErrorEndCondition(0.8))
                        .setEpochBatchSize(100)
                        .setUpdateCallback(Main::printStats)
                ).get();

        System.out.println("END");
        printStats(stats);

        try (var objectOutputStream = new ObjectOutputStream(new FileOutputStream("network-learned"))) {
            objectOutputStream.writeObject(network);
        }

//        printArray2D(outputs);
    }

    private static void printArray2D(double[][] array) {
        System.out.println("[");
        for (var arrayPart : array) {
            System.out.println("\t" + Arrays.toString(arrayPart) + ",");
        }
        System.out.println("]");
    }

    private static void printStats(LearningStatisticsProvider stats) {
        System.out.printf(
                "Error: %f; Accuracy: %f; Epochs: %d; Time: %ds\n",
                stats.getCurrentError(),
                stats.getCurrentAccuracy(),
                stats.getLearningEpochsCompletedCount(),
                stats.getTotalLearningTimeSeconds()
        );
    }
}
