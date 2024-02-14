package com.github.adrian99.neuralnetworkgui.component;

import com.github.adrian99.neuralnetwork.NeuralNetwork;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;

public class NetworkVisualizerComponent extends JComponent {
    private final List<List<Point>> neuronPoints = new ArrayList<>();
    private NeuralNetwork neuralNetwork;
    private int scale;
    private double weightsLowerBound;
    private double weightsUpperBound;

    public NetworkVisualizerComponent() {
        addComponentListener(new ResizeListener());
    }

    public void setNeuralNetwork(NeuralNetwork neuralNetwork) {
        this.neuralNetwork = neuralNetwork;
        calculateVisualizationProperties();
        repaint();
    }

    private void calculateVisualizationProperties() {
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
            scale = Math.min(spacingX, spacingY) / 30;

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
        neuronPoints.get(0).forEach(inputPoint -> drawNetworkInput(g, inputPoint));

        for (var i = 1; i < neuronPoints.size(); i++) {
            neuronPoints.get(i).forEach(neuronPoint -> drawNeuron(g, neuronPoint));
        }
    }

    private void drawWeightsAndBiases(Graphics2D g) {
        calculateWeightsBounds();

        for (var layerIndex = 0; layerIndex < neuralNetwork.getLayers().length; layerIndex++) {
            for (var neuronIndex = 0; neuronIndex < neuralNetwork.getLayers()[layerIndex].getNeurons().length; neuronIndex++) {
                var destinationPoint = neuronPoints.get(layerIndex + 1).get(neuronIndex);
                for (var weightIndex = 0; weightIndex < neuralNetwork.getLayers()[layerIndex].getNeurons()[neuronIndex].getWeights().length; weightIndex++) {
                    drawWeight(g, neuronPoints.get(layerIndex).get(weightIndex), destinationPoint, neuralNetwork.getLayers()[layerIndex].getNeurons()[neuronIndex].getWeights()[weightIndex]);
                }
                drawBias(g, destinationPoint, neuralNetwork.getLayers()[layerIndex].getNeurons()[neuronIndex].getBias());
            }
        }
    }

    private void calculateWeightsBounds() {
        weightsLowerBound = neuralNetwork.getLayers()[0].getNeurons()[0].getBias();
        weightsUpperBound = weightsLowerBound;

        for (var layer : neuralNetwork.getLayers()) {
            for (var neuron : layer.getNeurons()) {
                if (neuron.getBias() < weightsLowerBound) {
                    weightsLowerBound = neuron.getBias();
                }
                if (neuron.getBias() > weightsUpperBound) {
                    weightsUpperBound = neuron.getBias();
                }
                for (var weight : neuron.getWeights()) {
                    if (weight < weightsLowerBound) {
                        weightsLowerBound = weight;
                    }
                    if (weight > weightsUpperBound) {
                        weightsUpperBound = weight;
                    }
                }
            }
        }
    }

    private void drawNetworkInput(Graphics2D graphics, Point position) {
        graphics.setColor(Color.black);
        graphics.fillRect(position.x - 6 * scale, position.y - 6 * scale, 12 * scale, 12 * scale);
    }

    private void drawNeuron(Graphics2D graphics, Point position) {
        graphics.setColor(Color.DARK_GRAY);
        graphics.fillOval(position.x - 6 * scale, position.y - 6 * scale, 12 * scale, 12 * scale);
    }

    private void drawWeight(Graphics2D graphics,
                            Point source,
                            Point destination,
                            double value) {
        drawLine(graphics, source, destination, value, 0, 120, 240);
    }

    private void drawBias(Graphics2D graphics, Point neuronPosition, double value) {
        drawLine(graphics, new Point(neuronPosition.x - 5 * scale, neuronPosition.y - 10 * scale), neuronPosition, value, 0, 240, 120);
    }

    private void drawLine(Graphics2D graphics,
                          Point from,
                          Point to,
                          double value,
                          int baseColorR,
                          int baseColorG,
                          int baseColorB) {
        var valueScale = (value - weightsLowerBound) / (weightsUpperBound - weightsLowerBound);
        graphics.setColor(new Color(
                (int) (baseColorR + (255 - baseColorR) * (1 - valueScale)),
                (int) (baseColorG + (255 - baseColorG) * (1 - valueScale)),
                (int) (baseColorB + (255 - baseColorB) * (1 - valueScale)),
                (int) (255 * valueScale)
        ));
        graphics.setStroke(new BasicStroke((float) (scale * (valueScale + 1))));
        graphics.drawLine(from.x, from.y, to.x, to.y);
    }

    private static class ResizeListener extends ComponentAdapter {
        @Override
        public void componentResized(ComponentEvent e) {
            var component = (NetworkVisualizerComponent) e.getComponent();
            component.calculateVisualizationProperties();
            component.repaint();
        }
    }
}
