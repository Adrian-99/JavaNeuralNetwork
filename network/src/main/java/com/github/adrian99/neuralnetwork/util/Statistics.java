package com.github.adrian99.neuralnetwork.util;

import com.github.adrian99.neuralnetwork.learning.error.ErrorFunction;

public class Statistics {
    private Statistics() {}

    public static double accuracy(double[][] networkOutputs, int[][] targetOutputs) {
        var correctOutputs = 0;
        var allOutputs = 0;

        for (var i = 0; i < networkOutputs.length; i++) {
            for (var j = 0; j < networkOutputs[i].length; j++) {
                allOutputs++;
                if (Math.round(networkOutputs[i][j]) == targetOutputs[i][j]) {
                    correctOutputs++;
                }
            }
        }

        return allOutputs > 0 ?
                correctOutputs * 1.0 / allOutputs :
                0;
    }

    public static double error(double[][] networkOutputs, int[][] targetOutputs, ErrorFunction errorFunction) {
        var result = 0.0;
        for (var i = 0; i < networkOutputs.length; i++) {
            result += errorFunction.apply(networkOutputs[i], targetOutputs[i]);
        }
        return result;
    }
}
