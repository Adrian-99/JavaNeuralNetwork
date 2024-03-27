package com.github.adrian99.neuralnetwork.learning.data;

import com.github.adrian99.neuralnetwork.NeuralNetwork;
import com.github.adrian99.neuralnetwork.learning.error.ErrorFunction;
import com.github.adrian99.neuralnetwork.util.Statistics;

public class SimpleDataProvider extends DataProvider {
    private final LearningAndValidationData data;
    private NeuralNetwork neuralNetwork;
    private ErrorFunction errorFunction;
    private double[][] networkOutputs;
    private boolean networkOutputsUpdateRequired = true;

    public SimpleDataProvider(double[][] inputs, int[][] targets) {
        super(inputs, targets);
        var inputsAndTargets = new InputsAndTargets(inputs, targets);
        data = new LearningAndValidationData(inputsAndTargets, inputsAndTargets);
    }

    @Override
    public LearningAndValidationData getData() {
        return data;
    }

    @Override
    public void update(NeuralNetwork neuralNetwork, ErrorFunction errorFunction) {
        this.neuralNetwork = neuralNetwork;
        this.errorFunction = errorFunction;
        networkOutputsUpdateRequired = true;
    }

    @Override
    public double getAccuracy() {
        if (tryToUpdateNetworkOutputs()) {
            return Statistics.accuracy(networkOutputs, data.validationData().getTargets());
        } else {
            return 0;
        }
    }

    @Override
    public double getError() {
        if (tryToUpdateNetworkOutputs()) {
            return Statistics.error(networkOutputs, data.validationData().getTargets(), errorFunction);
        } else {
            return Double.MAX_VALUE;
        }
    }

    private boolean tryToUpdateNetworkOutputs() {
        if (networkOutputsUpdateRequired && neuralNetwork != null) {
            networkOutputs = neuralNetwork.activate(data.validationData().getInputs());
            networkOutputsUpdateRequired = false;
        }
        return networkOutputs != null;
    }
}
