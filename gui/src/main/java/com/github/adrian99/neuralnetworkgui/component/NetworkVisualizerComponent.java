package com.github.adrian99.neuralnetworkgui.component;

import com.github.adrian99.neuralnetwork.NeuralNetwork;
import com.github.adrian99.neuralnetworkgui.util.VisualizerUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class NetworkVisualizerComponent extends JComponent {
    private final List<List<Point>> neuronPoints = new ArrayList<>();
    private final transient Consumer<NeuronVisualizerComponent> onNeuronClicked;
    private NeuralNetwork neuralNetwork;
    private double[][][] weights;
    private double[][] biases;
    private transient VisualizerUtils visualizerUtils;

    public NetworkVisualizerComponent(Consumer<NeuronVisualizerComponent> onNeuronClicked) {
        this.onNeuronClicked = onNeuronClicked;
        addComponentListener(new ResizeListener());
        addMouseListener(new NeuronClickListener());
    }

    public void setNeuralNetwork(NeuralNetwork neuralNetwork) {
        this.neuralNetwork = neuralNetwork;
        visualizerUtils = new VisualizerUtils();
        calculateVisualizationProperties();
        repaint();
    }

    public void calculateVisualizationProperties() {
        neuronPoints.clear();
        if (neuralNetwork != null) {
            List<Integer> pointCounts = new ArrayList<>();

            pointCounts.add(neuralNetwork.getLayers()[0].getNeurons()[0].getWeights().length);
            var maxPointCount = pointCounts.get(0);
            for (var layer : neuralNetwork.getLayers()) {
                pointCounts.add(layer.getNeurons().length);
                if (layer.getNeurons().length > maxPointCount) {
                    maxPointCount = layer.getNeurons().length;
                }
            }

            var spacingX = (int) (getSize().getWidth() / (pointCounts.size() + 1));
            var spacingY = (int) (getSize().getHeight() / (maxPointCount + 1));
            visualizerUtils.setScale(Math.min(spacingX, spacingY) / 30);

            for (var i = 0; i < pointCounts.size(); i++) {
                var topSpace = (int) ((getSize().getHeight() - (pointCounts.get(i) - 1) * spacingY) / 2);
                neuronPoints.add(new ArrayList<>());
                for (var j = 0; j < pointCounts.get(i); j++) {
                    neuronPoints.get(i).add(new Point(spacingX * (i + 1), topSpace + spacingY * j));
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!neuronPoints.isEmpty()) {
            drawWeightsAndBiases((Graphics2D) g);
            drawInputsAndNeurons((Graphics2D) g);
        }
    }

    private void drawInputsAndNeurons(Graphics2D g) {
        neuronPoints.get(0).forEach(inputPoint -> visualizerUtils.drawNetworkInput(g, inputPoint));

        for (var i = 1; i < neuronPoints.size(); i++) {
            neuronPoints.get(i).forEach(neuronPoint -> visualizerUtils.drawNeuron(g, neuronPoint));
        }
    }

    private void drawWeightsAndBiases(Graphics2D g) {
        copyWeightsAndBiases();
        calculateWeightsBounds();

        for (var layerIndex = 0; layerIndex < weights.length; layerIndex++) {
            for (var neuronIndex = 0; neuronIndex < weights[layerIndex].length; neuronIndex++) {
                var destinationPoint = neuronPoints.get(layerIndex + 1).get(neuronIndex);
                for (var weightIndex = 0; weightIndex < weights[layerIndex][neuronIndex].length; weightIndex++) {
                    visualizerUtils.drawWeight(g, neuronPoints.get(layerIndex).get(weightIndex), destinationPoint, weights[layerIndex][neuronIndex][weightIndex]);
                }
                visualizerUtils.drawBias(g, destinationPoint, biases[layerIndex][neuronIndex]);
            }
        }
    }

    private void copyWeightsAndBiases() {
        var layers = neuralNetwork.getLayers();
        weights = new double[layers.length][][];
        biases = new double[layers.length][];
        for (var layerIndex = 0; layerIndex < layers.length; layerIndex++) {
            var neurons = layers[layerIndex].getNeurons();
            weights[layerIndex] = new double[neurons.length][];
            biases[layerIndex] = new double[neurons.length];
            for (var neuronIndex = 0; neuronIndex < neurons.length; neuronIndex++) {
                var neuronWeights = neurons[neuronIndex].getWeights();
                weights[layerIndex][neuronIndex] = new double[neuronWeights.length];
                System.arraycopy(neuronWeights, 0, weights[layerIndex][neuronIndex], 0, neuronWeights.length);
                biases[layerIndex][neuronIndex] = neurons[neuronIndex].getBias();
            }
        }
    }

    private void calculateWeightsBounds() {
        if (visualizerUtils.getWeightsLowerBound() == null) {
            visualizerUtils.setWeightsLowerBound(biases[0][0]);
        }
        if (visualizerUtils.getWeightsUpperBound() == null) {
            visualizerUtils.setWeightsUpperBound(visualizerUtils.getWeightsLowerBound());
        }

        for (var layerIndex = 0; layerIndex < weights.length; layerIndex++) {
            for (var neuronIndex = 0; neuronIndex < weights[layerIndex].length; neuronIndex++) {
                if (biases[layerIndex][neuronIndex] < visualizerUtils.getWeightsLowerBound()) {
                    visualizerUtils.setWeightsLowerBound(biases[layerIndex][neuronIndex]);
                }
                if (biases[layerIndex][neuronIndex] > visualizerUtils.getWeightsUpperBound()) {
                    visualizerUtils.setWeightsUpperBound(biases[layerIndex][neuronIndex]);
                }
                for (var weightIndex = 0; weightIndex < weights[layerIndex][neuronIndex].length; weightIndex++) {
                    if (weights[layerIndex][neuronIndex][weightIndex] < visualizerUtils.getWeightsLowerBound()) {
                        visualizerUtils.setWeightsLowerBound(weights[layerIndex][neuronIndex][weightIndex]);
                    }
                    if (weights[layerIndex][neuronIndex][weightIndex] > visualizerUtils.getWeightsUpperBound()) {
                        visualizerUtils.setWeightsUpperBound(weights[layerIndex][neuronIndex][weightIndex]);
                    }
                }
            }
        }
    }

    private static class ResizeListener extends ComponentAdapter {
        @Override
        public void componentResized(ComponentEvent e) {
            var component = (NetworkVisualizerComponent) e.getComponent();
            component.calculateVisualizationProperties();
            component.repaint();
        }
    }

    private class NeuronClickListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            for (var layerIndex = 0; layerIndex < neuronPoints.size() - 1; layerIndex++) {
                for (var neuronIndex = 0; neuronIndex < neuronPoints.get(layerIndex + 1).size(); neuronIndex++) {
                    var neuronPoint = neuronPoints.get(layerIndex + 1).get(neuronIndex);
                    if (Math.abs(neuronPoint.getX() - e.getX()) < 6 * visualizerUtils.getScale() &&
                            Math.abs(neuronPoint.getY() - e.getY()) < 6 * visualizerUtils.getScale()) {
                        onNeuronClicked.accept(new NeuronVisualizerComponent(neuralNetwork, layerIndex, neuronIndex, visualizerUtils));
                        return;
                    }
                }
            }
        }
    }
}
