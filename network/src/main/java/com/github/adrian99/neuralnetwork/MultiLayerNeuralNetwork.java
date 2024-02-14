package com.github.adrian99.neuralnetwork;

import com.github.adrian99.neuralnetwork.layer.HiddenNeuronsLayer;
import com.github.adrian99.neuralnetwork.layer.InputNeuronsLayer;
import com.github.adrian99.neuralnetwork.layer.NeuronsLayer;
import com.github.adrian99.neuralnetwork.layer.OutputNeuronsLayer;
import com.github.adrian99.neuralnetwork.learning.LearningFunction;
import com.github.adrian99.neuralnetwork.learning.error.ErrorFunction;

public class MultiLayerNeuralNetwork extends NeuralNetwork {
    private final InputNeuronsLayer inputLayer;
    private final HiddenNeuronsLayer[] hiddenLayers;
    private final OutputNeuronsLayer outputLayer;

    public MultiLayerNeuralNetwork(InputNeuronsLayer inputLayer,
                                   OutputNeuronsLayer outputLayer,
                                   HiddenNeuronsLayer... hiddenLayers) {
        this.inputLayer = inputLayer;
        this.hiddenLayers = hiddenLayers;
        this.outputLayer = outputLayer;
        if (hiddenLayers.length > 0) {
            inputLayer.setNextLayer(hiddenLayers[0]);
            outputLayer.setPreviousLayer(hiddenLayers[hiddenLayers.length - 1]);
            if (hiddenLayers.length > 1) {
                for (var i = 0; i < hiddenLayers.length; i++) {
                    if (i == 0) {
                        hiddenLayers[i].setSurroundingLayers(inputLayer, hiddenLayers[i + 1]);
                    } else if (i == hiddenLayers.length - 1) {
                        hiddenLayers[i].setSurroundingLayers(hiddenLayers[i - 1], outputLayer);
                    } else {
                        hiddenLayers[i].setSurroundingLayers(hiddenLayers[i - 1], hiddenLayers[i + 1]);
                    }
                }
            } else {
                hiddenLayers[0].setSurroundingLayers(inputLayer, outputLayer);
            }
        } else {
            inputLayer.setNextLayer(outputLayer);
            outputLayer.setPreviousLayer(inputLayer);
        }
    }

    @Override
    public NeuronsLayer[] getLayers() {
        var layers = new NeuronsLayer[hiddenLayers.length + 2];
        layers[0] = inputLayer;
        System.arraycopy(hiddenLayers, 0, layers, 1, hiddenLayers.length);
        layers[hiddenLayers.length + 1] = outputLayer;
        return layers;
    }

    @Override
    public double[] activate(double[] inputs) {
        inputLayer.activate(inputs);
        for (var hiddenLayer : hiddenLayers) {
            hiddenLayer.activate();
        }
        return outputLayer.activate();
    }

    @Override
    protected void calculateNeuronErrors(ErrorFunction errorFunction, double[] targetOutputs) {
        outputLayer.calculateNeuronErrors(errorFunction, targetOutputs);
        for (var i = hiddenLayers.length - 1; i >= 0; i--) {
            hiddenLayers[i].calculateNeuronErrors();
        }
        inputLayer.calculateNeuronErrors();
    }

    @Override
    protected void calculateNewNeuronWeights(LearningFunction learningFunction, double[] inputs) {
        inputLayer.calculateNewNeuronWeights(learningFunction, inputs);
        for (var hiddenLayer : hiddenLayers) {
            hiddenLayer.calculateNewNeuronWeights(learningFunction);
        }
        outputLayer.calculateNewNeuronWeights(learningFunction);
    }
}
