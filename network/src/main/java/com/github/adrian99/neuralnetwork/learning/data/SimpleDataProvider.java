package com.github.adrian99.neuralnetwork.learning.data;

import com.github.adrian99.neuralnetwork.NeuralNetwork;
import com.github.adrian99.neuralnetwork.learning.error.ErrorFunction;
import com.github.adrian99.neuralnetwork.util.Statistics;

public class SimpleDataProvider extends DataProvider {
    private final InputsAndTargets data;

    public SimpleDataProvider(double[][] inputs, int[][] targets) {
        super(inputs, targets);
        data = new InputsAndTargets(inputs, targets);
    }

    @Override
    public InputsAndTargets getLearningData() {
        return data;
    }

    @Override
    public void performValidation(NeuralNetwork neuralNetwork, ErrorFunction errorFunction) {
        var networkOutputs = neuralNetwork.activate(data.getInputs());
        accuracy = Statistics.accuracy(networkOutputs, data.getTargets());
        error = Statistics.error(networkOutputs, data.getTargets(), errorFunction);
    }
}
