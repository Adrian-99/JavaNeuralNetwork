package com.github.adrian99.neuralnetworkgui.window;

import com.github.adrian99.neuralnetwork.NeuralNetwork;
import com.github.adrian99.neuralnetwork.layer.neuron.activation.ActivationFunction;
import com.github.adrian99.neuralnetwork.layer.neuron.activation.LinearActivationFunction;
import com.github.adrian99.neuralnetwork.layer.neuron.activation.LogisticActivationFunction;
import com.github.adrian99.neuralnetwork.layer.neuron.activation.UnitStepActivationFunction;
import com.github.adrian99.neuralnetwork.layer.neuron.weightinitialization.NormalizedXavierWeightInitializationFunction;
import com.github.adrian99.neuralnetwork.layer.neuron.weightinitialization.WeightInitializationFunction;
import com.github.adrian99.neuralnetwork.layer.neuron.weightinitialization.XavierWeightInitializationFunction;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.util.List;

public class NetworkWindow extends JFrame {
    private final JButton exportNetworkButton;
    private final JFileChooser fileChooser = new JFileChooser();

    private NeuralNetwork neuralNetwork = null;

    public NetworkWindow() {
        setTitle("Neural network");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        var topButtonsPanel = new JPanel();
        topButtonsPanel.setLayout(new FlowLayout());
        getContentPane().add(topButtonsPanel, BorderLayout.NORTH);
        var newNetworkButton = new JButton("New network");
        newNetworkButton.addActionListener(event -> onNewNetwork());
        topButtonsPanel.add(newNetworkButton);
        var importNetworkButton = new JButton("Import network");
        importNetworkButton.addActionListener(event -> onImportNetwork());
        topButtonsPanel.add(importNetworkButton);
        exportNetworkButton = new JButton("Export network");
        exportNetworkButton.addActionListener(event -> onExportNetwork());
        topButtonsPanel.add(exportNetworkButton);
        updateExportNetworkButton();

        setVisible(true);
        pack();
    }

    private void onNewNetwork() {
        new NetworkCreatorWindow(networkData -> {
            var lastLayerData = networkData.layersData().get(networkData.layersData().size() - 1);
            var networkBuilder = new NeuralNetwork.Builder(networkData.inputsCount(), lastLayerData.neuronsCount());

            for (var i = 0; i < networkData.layersData().size() - 1; i++) {
                var layerData = networkData.layersData().get(i);
                networkBuilder.addLayer(
                        layerData.neuronsCount(),
                        getActivationFunction(layerData.activationFunctionClass(), layerData.activationFunctionParameters()),
                        getWeightInitializationFunction(layerData.weightInitializationFunctionClass())
                );
            }

            neuralNetwork = networkBuilder.addOutputLayer(
                    getActivationFunction(lastLayerData.activationFunctionClass(), lastLayerData.activationFunctionParameters()),
                    getWeightInitializationFunction(lastLayerData.weightInitializationFunctionClass())
            );

            updateExportNetworkButton();
        });
    }

    private void onImportNetwork() {
        if (fileChooser.showOpenDialog(getContentPane()) == JFileChooser.APPROVE_OPTION) {
            var file = fileChooser.getSelectedFile();
            try (var objectInputStream = new ObjectInputStream(new FileInputStream(file))) {
                neuralNetwork = (NeuralNetwork) objectInputStream.readObject();
            } catch (IOException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(
                        getContentPane(),
                        "Error occurred while importing network from file: " + e.getMessage(),
                        "Importing error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
            updateExportNetworkButton();
        }
    }

    private void onExportNetwork() {
        if (neuralNetwork != null && fileChooser.showSaveDialog(getContentPane()) == JFileChooser.APPROVE_OPTION) {
            var file = fileChooser.getSelectedFile();
            try (var objectOutputStream = new ObjectOutputStream(new FileOutputStream(file))) {
                objectOutputStream.writeObject(neuralNetwork);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(
                        getContentPane(),
                        "Error occurred while exporting network to file: " + e.getMessage(),
                        "Exporting error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private ActivationFunction getActivationFunction(String functionName, List<Double> functionParameters) {
        if (functionName.equals(LinearActivationFunction.class.getSimpleName())) {
            return new LinearActivationFunction(functionParameters.get(0), functionParameters.get(1));
        } else if (functionName.equals(LogisticActivationFunction.class.getSimpleName())) {
            return new LogisticActivationFunction(functionParameters.get(0), functionParameters.get(1));
        } else if (functionName.equals(UnitStepActivationFunction.class.getSimpleName())) {
            return new UnitStepActivationFunction();
        } else {
            throw new IllegalArgumentException("Unknown activation function class: " + functionName);
        }
    }

    private WeightInitializationFunction getWeightInitializationFunction(String functionName) {
        if (functionName.equals(NormalizedXavierWeightInitializationFunction.class.getSimpleName())) {
            return new NormalizedXavierWeightInitializationFunction();
        } else if (functionName.equals(XavierWeightInitializationFunction.class.getSimpleName())) {
            return new XavierWeightInitializationFunction();
        } else {
            throw new IllegalArgumentException("Unknown weight initialization function class: " + functionName);
        }
    }

    private void updateExportNetworkButton() {
        exportNetworkButton.setEnabled(neuralNetwork != null);
    }
}
