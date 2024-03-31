package com.github.adrian99.neuralnetworkgui.component;

import com.github.adrian99.neuralnetwork.NeuralNetwork;
import com.github.adrian99.neuralnetworkgui.util.VisualizerUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;

public class NeuronVisualizerComponent extends JComponent {
    private final List<Point> inputWeightsStartPoints = new ArrayList<>();
    private final List<Point> outputWeightsEndPoints = new ArrayList<>();
    private final NeuralNetwork neuralNetwork;
    private final int layerIndex;
    private final int neuronIndex;
    private final transient VisualizerUtils visualizerUtils;
    private double[] inputWeights;
    private double [] outputWeights;
    private double bias;
    private Point neuronPoint;

    public NeuronVisualizerComponent(NeuralNetwork neuralNetwork, int layerIndex, int neuronIndex, VisualizerUtils visualizerUtils) {
        this.neuralNetwork = neuralNetwork;
        this.layerIndex = layerIndex;
        this.neuronIndex = neuronIndex;
        this.visualizerUtils = visualizerUtils;
        addComponentListener(new ResizeListener());
        calculateVisualizationProperties();
    }

    private void calculateVisualizationProperties() {
        inputWeightsStartPoints.clear();
        outputWeightsEndPoints.clear();
        if (neuralNetwork != null) {
            var neuron = neuralNetwork.getLayers()[layerIndex].getNeurons()[neuronIndex];
            var inputWeightsCount = neuron.getWeights().length;
            var outputWeightsCount = neuralNetwork.getLayers().length > layerIndex + 1 ?
                    neuralNetwork.getLayers()[layerIndex + 1].getNeurons().length :
                    0;

            neuronPoint = new Point((int) (getSize().getWidth() / 2), (int) (getSize().getHeight() / 2));

            var spacingY = (int) (getSize().getHeight() / (Math.max(inputWeightsCount, outputWeightsCount) + 1));
            visualizerUtils.setScale(spacingY / 15);

            var topSpace = (int) ((getSize().getHeight() - (inputWeightsCount - 1) * spacingY) / 2);
            for (var i = 0; i < inputWeightsCount; i++) {
                inputWeightsStartPoints.add(new Point(0, topSpace + spacingY * i));
            }

            if (outputWeightsCount > 0) {
                topSpace = (int) ((getSize().getHeight() - (outputWeightsCount - 1) * spacingY) / 2);
                for (var i = 0; i < outputWeightsCount; i++) {
                    outputWeightsEndPoints.add(new Point((int) getSize().getWidth(), topSpace + spacingY * i));
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawWeightsAndBias((Graphics2D) g);
        visualizerUtils.drawNeuron((Graphics2D) g, neuronPoint);
    }

    private void drawWeightsAndBias(Graphics2D g) {
        copyWeightsAndBias();
        calculateWeightsBounds();

        for (var inputWeightIndex = 0; inputWeightIndex < inputWeights.length; inputWeightIndex++) {
            visualizerUtils.drawWeight(g, inputWeightsStartPoints.get(inputWeightIndex), neuronPoint, inputWeights[inputWeightIndex]);
        }
        for (var outputWeightIndex = 0; outputWeightIndex < outputWeights.length; outputWeightIndex++) {
            visualizerUtils.drawWeight(g, neuronPoint, outputWeightsEndPoints.get(outputWeightIndex), outputWeights[outputWeightIndex]);
        }
        visualizerUtils.drawBias(g, neuronPoint, bias);
    }

    private void copyWeightsAndBias() {
        var neuron = neuralNetwork.getLayers()[layerIndex].getNeurons()[neuronIndex];
        inputWeights = new double[neuron.getWeights().length];
        System.arraycopy(neuron.getWeights(), 0, inputWeights, 0, neuron.getWeights().length);
        bias = neuron.getBias();
        if (neuralNetwork.getLayers().length > layerIndex + 1) {
            var layer = neuralNetwork.getLayers()[layerIndex + 1];
            outputWeights = new double[layer.getNeurons().length];
            for (var i = 0; i < layer.getNeurons().length; i++) {
                outputWeights[i] = layer.getNeurons()[i].getWeights()[neuronIndex];
            }
        } else {
            outputWeights = new double[0];
        }
    }

    private void calculateWeightsBounds() {
        if (visualizerUtils.getWeightsLowerBound() == null) {
            visualizerUtils.setWeightsLowerBound(bias);
        }
        if (visualizerUtils.getWeightsUpperBound() == null) {
            visualizerUtils.setWeightsUpperBound(visualizerUtils.getWeightsLowerBound());
        }
        for (var weight : inputWeights) {
            if (weight < visualizerUtils.getWeightsLowerBound()) {
                visualizerUtils.setWeightsLowerBound(weight);
            }
            if (weight > visualizerUtils.getWeightsUpperBound()) {
                visualizerUtils.setWeightsUpperBound(weight);
            }
        }
        for (var weight : outputWeights) {
            if (weight < visualizerUtils.getWeightsLowerBound()) {
                visualizerUtils.setWeightsLowerBound(weight);
            }
            if (weight > visualizerUtils.getWeightsUpperBound()) {
                visualizerUtils.setWeightsUpperBound(weight);
            }
        }
        if (bias < visualizerUtils.getWeightsLowerBound()) {
            visualizerUtils.setWeightsLowerBound(bias);
        }
        if (bias > visualizerUtils.getWeightsUpperBound()) {
            visualizerUtils.setWeightsUpperBound(bias);
        }
    }

    private static class ResizeListener extends ComponentAdapter {
        @Override
        public void componentResized(ComponentEvent e) {
            var component = (NeuronVisualizerComponent) e.getComponent();
            component.calculateVisualizationProperties();
            component.repaint();
        }
    }
}
