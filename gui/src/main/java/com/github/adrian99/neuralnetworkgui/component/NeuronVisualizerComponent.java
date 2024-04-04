package com.github.adrian99.neuralnetworkgui.component;

import com.github.adrian99.neuralnetwork.NeuralNetwork;
import com.github.adrian99.neuralnetwork.layer.neuron.activation.LinearActivationFunction;
import com.github.adrian99.neuralnetwork.layer.neuron.activation.LogisticActivationFunction;
import com.github.adrian99.neuralnetworkgui.util.VisualizerUtils;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class NeuronVisualizerComponent extends JComponent {
    private final List<Point> inputWeightsStartPoints = new ArrayList<>();
    private final List<Point> outputWeightsEndPoints = new ArrayList<>();
    private final NeuralNetwork neuralNetwork;
    private final int layerIndex;
    private final int neuronIndex;
    private final transient VisualizerUtils visualizerUtils;
    private transient Image activationFunctionImage;
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

            var spacingY = (int) (getSize().getHeight() / (Math.max(inputWeightsCount, outputWeightsCount) + 1));

            if (visualizerUtils.setScale(spacingY / 7) && visualizerUtils.getScale() > 0) {
                getActivationFunctionImage()
                        .thenAccept(image -> activationFunctionImage = image)
                        .thenRun(this::repaint);
            }

            neuronPoint = new Point((int) (getSize().getWidth() / 2), (int) (getSize().getHeight() / 2));

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

    private CompletableFuture<Image> getActivationFunctionImage() {
        return CompletableFuture.supplyAsync(() -> {
            var neuron = neuralNetwork.getLayers()[layerIndex].getNeurons()[neuronIndex];
            TeXFormula teXFormula = null;
            var decimalFormat = new DecimalFormat("#.###");
            if (neuron.getActivationFunction() instanceof LinearActivationFunction linearActivationFunction) {
                teXFormula = new TeXFormula("f = %s \\cdot x + %s".formatted(
                        decimalFormat.format(linearActivationFunction.getSlope()),
                        decimalFormat.format(linearActivationFunction.getIntercept())
                ));
            } else if (neuron.getActivationFunction() instanceof LogisticActivationFunction logisticActivationFunction) {
                teXFormula = new TeXFormula("f = \\frac{%s}{1 + e^{-%s \\cdot x}}".formatted(
                        decimalFormat.format(logisticActivationFunction.getSupremum()),
                        decimalFormat.format(logisticActivationFunction.getGrowthRate())
                ));
            }
            if (teXFormula != null) {
                return teXFormula.createBufferedImage(
                        TeXConstants.STYLE_DISPLAY,
                        visualizerUtils.getScale() * 1.5f,
                        Color.white,
                        Color.darkGray
                );
            } else {
                return null;
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawWeightsAndBias((Graphics2D) g);
        visualizerUtils.drawNeuron((Graphics2D) g, neuronPoint);
        if (activationFunctionImage != null) {
            g.drawImage(
                    activationFunctionImage,
                    (int) neuronPoint.getX() - (activationFunctionImage.getWidth(null) / 2),
                    (int) neuronPoint.getY() - (activationFunctionImage.getHeight(null) / 2),
                    null
            );
        }
    }

    private void drawWeightsAndBias(Graphics2D g) {
        copyWeightsAndBias();
        calculateWeightsAbsBound();

        for (var inputWeightIndex = 0; inputWeightIndex < inputWeights.length; inputWeightIndex++) {
            visualizerUtils.drawWeight(g, inputWeightsStartPoints.get(inputWeightIndex), neuronPoint, inputWeights[inputWeightIndex], true);
        }
        for (var outputWeightIndex = 0; outputWeightIndex < outputWeights.length; outputWeightIndex++) {
            visualizerUtils.drawWeight(g, outputWeightsEndPoints.get(outputWeightIndex), neuronPoint, outputWeights[outputWeightIndex], true);
        }
        visualizerUtils.drawBias(g, neuronPoint, bias, true);
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

    private void calculateWeightsAbsBound() {
        if (visualizerUtils.getWeightsAbsBound() == null) {
            visualizerUtils.setWeightsAbsBound(Math.abs(bias));
        }
        for (var weight : inputWeights) {
            if (Math.abs(weight) > visualizerUtils.getWeightsAbsBound()) {
                visualizerUtils.setWeightsAbsBound(Math.abs(weight));
            }
        }
        for (var weight : outputWeights) {
            if (Math.abs(weight) > visualizerUtils.getWeightsAbsBound()) {
                visualizerUtils.setWeightsAbsBound(Math.abs(weight));
            }
        }
        if (Math.abs(bias) > visualizerUtils.getWeightsAbsBound()) {
            visualizerUtils.setWeightsAbsBound(Math.abs(bias));
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
