package com.github.adrian99.neuralnetworkgui.window;

import com.github.adrian99.neuralnetwork.NeuralNetwork;
import com.github.adrian99.neuralnetwork.learning.data.InputsAndTargets;
import com.github.adrian99.neuralnetwork.learning.error.ErrorFunction;
import com.github.adrian99.neuralnetworkgui.util.DataImportUtils;
import com.github.adrian99.neuralnetworkgui.util.WindowUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class NetworkTestTypeConfiguratorWindow extends JDialog {
    private final NeuralNetwork neuralNetwork;
    private final transient InputsAndTargets learningData;
    private final transient ErrorFunction errorFunction;

    public NetworkTestTypeConfiguratorWindow(NeuralNetwork neuralNetwork, InputsAndTargets learningData, ErrorFunction errorFunction) {
        this.neuralNetwork = neuralNetwork;
        this.learningData = learningData;
        this.errorFunction = errorFunction;

        setIconImage(WindowUtils.getIconImage());
        setTitle("Type of test of neural network");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setModalityType(ModalityType.APPLICATION_MODAL);

        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        var useLearningDataButton = new JButton("Use learning data");
        useLearningDataButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        useLearningDataButton.addActionListener(e -> onUseLearningData());
        useLearningDataButton.setEnabled(learningData != null);
        getContentPane().add(useLearningDataButton);

        var importDataButton = new JButton("Import data from file");
        importDataButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        importDataButton.addActionListener(e -> onImportData());
        getContentPane().add(importDataButton);

        var manualTestButton = new JButton("Manually input data");
        manualTestButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        manualTestButton.addActionListener(e -> onManualTest());
        getContentPane().add(manualTestButton);

        pack();
        setVisible(true);
    }

    private void onUseLearningData() {
        new NetworkAutomaticTesterWindow(neuralNetwork, learningData, errorFunction);
    }

    private void onImportData() {
        DataImportUtils.importFromFile(new JFileChooser(), getContentPane(), neuralNetwork, (file, data) ->
            new NetworkAutomaticTesterWindow(neuralNetwork, data, errorFunction)
        );
    }

    private void onManualTest() {
        new NetworkManualTesterWindow(neuralNetwork);
    }
}
