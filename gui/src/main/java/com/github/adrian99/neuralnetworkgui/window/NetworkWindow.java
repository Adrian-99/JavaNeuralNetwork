package com.github.adrian99.neuralnetworkgui.window;

import com.github.adrian99.neuralnetwork.NeuralNetwork;
import com.github.adrian99.neuralnetwork.data.csv.CsvDataLoader;
import com.github.adrian99.neuralnetwork.layer.neuron.activation.ActivationFunction;
import com.github.adrian99.neuralnetwork.layer.neuron.activation.LinearActivationFunction;
import com.github.adrian99.neuralnetwork.layer.neuron.activation.LogisticActivationFunction;
import com.github.adrian99.neuralnetwork.layer.neuron.activation.UnitStepActivationFunction;
import com.github.adrian99.neuralnetwork.layer.neuron.weightinitialization.NormalizedXavierWeightInitializationFunction;
import com.github.adrian99.neuralnetwork.layer.neuron.weightinitialization.WeightInitializationFunction;
import com.github.adrian99.neuralnetwork.layer.neuron.weightinitialization.XavierWeightInitializationFunction;
import com.github.adrian99.neuralnetwork.learning.BackPropagationLearningFunction;
import com.github.adrian99.neuralnetwork.learning.data.CrossValidationDataProvider;
import com.github.adrian99.neuralnetwork.learning.data.DataProvider;
import com.github.adrian99.neuralnetwork.learning.data.SimpleDataProvider;
import com.github.adrian99.neuralnetwork.learning.endcondition.AccuracyEndCondition;
import com.github.adrian99.neuralnetwork.learning.endcondition.EpochsCountEndCondition;
import com.github.adrian99.neuralnetwork.learning.endcondition.ErrorEndCondition;
import com.github.adrian99.neuralnetwork.learning.endcondition.TimeEndCondition;
import com.github.adrian99.neuralnetwork.learning.error.SumSquaredErrorFunction;
import com.github.adrian99.neuralnetwork.learning.supervisor.LearningStatisticsProvider;
import com.github.adrian99.neuralnetwork.learning.supervisor.LearningSupervisor;
import com.github.adrian99.neuralnetworkgui.component.NetworkVisualizerComponent;
import com.github.adrian99.neuralnetworkgui.data.LearningConfigurationData;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class NetworkWindow extends JFrame {
    private final JButton exportNetworkButton;
    private final JButton newNetworkButton;
    private final JButton importNetworkButton;
    private final JButton importDataButton;
    private final JButton startLearningButton;
    private final JButton pauseLearningButton;
    private final JButton resumeLearningButton;
    private final JButton testNetworkButton;
    private final JPanel topInfoPanel1;
    private final JPanel topInfoPanel2;
    private final NetworkVisualizerComponent networkVisualizerComponent;
    private final JPanel bottomInfoPanel;
    private final JLabel bottomEpochsLabel;
    private final JLabel bottomTimeLabel;
    private final JLabel bottomAccuracyLabel;
    private final JLabel bottomErrorLabel;
    private final JFileChooser fileChooser = new JFileChooser();

    private NeuralNetwork neuralNetwork = null;
    private String dataImportInfo;
    private double[][] inputData;
    private int[][] outputData;
    private Integer previousCrossValidationGroupsCount = null;
    private transient LearningConfigurationData learningConfigurationData = null;
    private transient LearningSupervisor.Configuration learningConfiguration = null;
    private transient LearningSupervisor learningSupervisor = null;
    private transient CompletableFuture<LearningStatisticsProvider> learningFuture = null;

    public NetworkWindow() {
        setTitle("Neural network");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
        addWindowListener(new CloseListener());

        var topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        getContentPane().add(topPanel, BorderLayout.NORTH);

        var topButtonsPanel = new JPanel();
        topButtonsPanel.setLayout(new FlowLayout());
        topPanel.add(topButtonsPanel);
        newNetworkButton = new JButton("New network");
        newNetworkButton.addActionListener(event -> onNewNetwork());
        topButtonsPanel.add(newNetworkButton);
        importNetworkButton = new JButton("Import network");
        importNetworkButton.addActionListener(event -> onImportNetwork());
        topButtonsPanel.add(importNetworkButton);
        exportNetworkButton = new JButton("Export network");
        exportNetworkButton.addActionListener(event -> onExportNetwork());
        topButtonsPanel.add(exportNetworkButton);

        topButtonsPanel.add(new JSeparator(SwingConstants.VERTICAL));

        importDataButton = new JButton("Import data");
        importDataButton.addActionListener(event -> onImportData());
        topButtonsPanel.add(importDataButton);

        topButtonsPanel.add(new JSeparator(SwingConstants.VERTICAL));

        startLearningButton = new JButton("Start learning");
        startLearningButton.addActionListener(event -> onStartLearning());
        topButtonsPanel.add(startLearningButton);
        pauseLearningButton = new JButton("Pause learning");
        pauseLearningButton.addActionListener(event -> onPauseLearning());
        topButtonsPanel.add(pauseLearningButton);
        resumeLearningButton = new JButton("Resume learning");
        resumeLearningButton.addActionListener(event -> onResumeLearning());
        topButtonsPanel.add(resumeLearningButton);

        topButtonsPanel.add(new JSeparator(SwingConstants.VERTICAL));

        testNetworkButton = new JButton("Test network");
        testNetworkButton.addActionListener(event -> onTestNetwork());
        topButtonsPanel.add(testNetworkButton);

        updateButtons();

        topInfoPanel1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        topPanel.add(topInfoPanel1);
        topInfoPanel2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        topPanel.add(topInfoPanel2);

        updateTopInfo();

        networkVisualizerComponent = new NetworkVisualizerComponent();
        networkVisualizerComponent.setPreferredSize(new Dimension(1080, 720));
        getContentPane().add(networkVisualizerComponent, BorderLayout.CENTER);

        bottomInfoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        getContentPane().add(bottomInfoPanel, BorderLayout.SOUTH);

        var valueLabelsDimension = new Dimension(50, 20);
        bottomEpochsLabel = new JLabel();
        bottomEpochsLabel.setPreferredSize(valueLabelsDimension);
        bottomEpochsLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        bottomTimeLabel = new JLabel();
        bottomTimeLabel.setPreferredSize(valueLabelsDimension);
        bottomTimeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        bottomAccuracyLabel = new JLabel();
        bottomAccuracyLabel.setPreferredSize(valueLabelsDimension);
        bottomAccuracyLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        bottomErrorLabel = new JLabel();
        bottomErrorLabel.setPreferredSize(valueLabelsDimension);
        bottomErrorLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        var spacerDimension = new Dimension(25, 20);
        bottomInfoPanel.add(new JLabel("Epochs:"));
        bottomInfoPanel.add(bottomEpochsLabel);
        bottomInfoPanel.add(Box.createRigidArea(spacerDimension));
        bottomInfoPanel.add(new JLabel("Time:"));
        bottomInfoPanel.add(bottomTimeLabel);
        bottomInfoPanel.add(Box.createRigidArea(spacerDimension));
        bottomInfoPanel.add(new JLabel("Accuracy:"));
        bottomInfoPanel.add(bottomAccuracyLabel);
        bottomInfoPanel.add(Box.createRigidArea(spacerDimension));
        bottomInfoPanel.add(new JLabel("Error:"));
        bottomInfoPanel.add(bottomErrorLabel);

        updateBottomInfo(null);

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
            networkVisualizerComponent.setNeuralNetwork(neuralNetwork);
            inputData = null;
            outputData = null;
            previousCrossValidationGroupsCount = null;
            learningConfigurationData = null;
            learningSupervisor = null;
            learningFuture = null;

            updateButtons();
            updateTopInfo();
            updateBottomInfo(null);
        });
    }

    private void onImportNetwork() {
        if (fileChooser.showOpenDialog(getContentPane()) == JFileChooser.APPROVE_OPTION) {
            var file = fileChooser.getSelectedFile();
            try (var objectInputStream = new ObjectInputStream(new FileInputStream(file))) {
                neuralNetwork = (NeuralNetwork) objectInputStream.readObject();
                networkVisualizerComponent.setNeuralNetwork(neuralNetwork);
                inputData = null;
                outputData = null;
                previousCrossValidationGroupsCount = null;
                learningConfigurationData = null;
                learningSupervisor = null;
                learningFuture = null;
            } catch (IOException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(
                        getContentPane(),
                        "Error occurred while importing network from file:\n" + e.getMessage(),
                        "Importing error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
            updateButtons();
            updateTopInfo();
            updateBottomInfo(null);
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
                        "Error occurred while exporting network to file:\n" + e.getMessage(),
                        "Exporting error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void onImportData() {
        if (neuralNetwork != null && fileChooser.showOpenDialog(getContentPane()) == JFileChooser.APPROVE_OPTION) {
            var file = fileChooser.getSelectedFile();
            try {
                var numericData = new CsvDataLoader(file).toNumericData();
                var networkInputsCount = neuralNetwork.getLayers()[0].getNeurons()[0].getWeights().length;
                var networkOutputsCount = neuralNetwork.getLayers()[neuralNetwork.getLayers().length - 1].getNeurons().length;
                if (numericData.getColumnsCount() >= networkInputsCount + networkOutputsCount) {
                    inputData = numericData.toDoubleArray(0, networkInputsCount - 1);
                    outputData = numericData.toIntArray(networkInputsCount, networkInputsCount + networkOutputsCount - 1);
                    dataImportInfo = "Learning data: %s (%d rows)".formatted(file.getName(), inputData.length);
                    learningSupervisor = null;
                    learningFuture = null;
                    previousCrossValidationGroupsCount = null;
                    learningConfigurationData = null;
                } else {
                    JOptionPane.showMessageDialog(
                            getContentPane(),
                            "Not enough columns of data:\n" +
                                    "Neural network has " + networkInputsCount + " inputs and " + networkOutputsCount + " outputs\n" +
                                    "Expected at least " + (networkInputsCount + networkOutputsCount) + " columns of data, got " + numericData.getColumnsCount(),
                            "Incorrect data error",
                            JOptionPane.WARNING_MESSAGE
                    );
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        getContentPane(),
                        "Error occurred while importing data from file:\n" + e.getMessage(),
                        "Importing error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
            updateButtons();
            updateTopInfo();
            updateBottomInfo(null);
        }
    }

    private void onStartLearning() {
        if (learningFuture == null || learningFuture.isDone()) {
            if (learningConfigurationData != null) {
                new LearningConfiguratorWindow(learningConfigurationData, this::startLearningCallback, inputData.length);
            } else {
                new LearningConfiguratorWindow(this::startLearningCallback, inputData.length);
            }
        }
    }

    private void startLearningCallback(LearningConfigurationData c) {
        if (learningSupervisor == null ||
                previousCrossValidationGroupsCount == null ||
                !previousCrossValidationGroupsCount.equals(c.crossValidationGroupsCount().orElse(1))) {
            previousCrossValidationGroupsCount = c.crossValidationGroupsCount().orElse(1);
            var dataProvider = c.crossValidationGroupsCount()
                    .map(groupsCount -> (DataProvider) new CrossValidationDataProvider(inputData, outputData, groupsCount))
                    .orElse(new SimpleDataProvider(inputData, outputData));
            learningSupervisor = new LearningSupervisor(neuralNetwork, dataProvider);
        }
        learningConfigurationData = c;

        learningConfiguration = new LearningSupervisor.Configuration(
                new SumSquaredErrorFunction(),
                new BackPropagationLearningFunction(learningConfigurationData.learningRate())
        ).setUpdateCallback(s -> {
            networkVisualizerComponent.repaint();
            updateBottomInfo(s);
            if (!s.isLearningInProgress()) {
                updateTopInfo();
                updateButtons();
            }
        }).setEpochBatchSize(learningConfigurationData.epochBatchSize());
        learningConfigurationData.accuracyEndConditionValue()
                .ifPresent(v -> learningConfiguration.addEndCondition(new AccuracyEndCondition(v)));
        learningConfigurationData.epochsCountEndConditionValue()
                .ifPresent(v -> learningConfiguration.addEndCondition(new EpochsCountEndCondition(v)));
        learningConfigurationData.errorEndConditionValue()
                .ifPresent(v -> learningConfiguration.addEndCondition(new ErrorEndCondition(v)));
        learningConfigurationData.timeEndConditionValue()
                .ifPresent(v -> learningConfiguration.addEndCondition(new TimeEndCondition(v)));

        learningFuture = learningSupervisor.startLearningAsync(learningConfiguration);
        updateButtons();
        updateTopInfo();
    }

    private void onPauseLearning() {
        if (learningFuture != null && !learningFuture.isDone()) {
            learningFuture.cancel(true);
        }
    }

    private void onResumeLearning() {
        if (learningFuture != null && learningFuture.isCancelled()) {
            learningFuture = learningSupervisor.startLearningAsync(learningConfiguration);
            updateButtons();
            updateTopInfo();
        }
    }

    private void onTestNetwork() {
        new NetworkTesterWindow(neuralNetwork);
    }

    private void updateButtons() {
        newNetworkButton.setEnabled(learningFuture == null || learningFuture.isDone());
        importNetworkButton.setEnabled(learningFuture == null || learningFuture.isDone());
        exportNetworkButton.setEnabled(neuralNetwork != null && (learningFuture == null || learningFuture.isDone()));
        importDataButton.setEnabled(neuralNetwork != null && (learningFuture == null || learningFuture.isDone()));
        startLearningButton.setEnabled(neuralNetwork != null && inputData != null && outputData != null && (learningFuture == null || learningFuture.isDone()));
        pauseLearningButton.setEnabled(learningFuture != null && !learningFuture.isDone());
        resumeLearningButton.setEnabled(learningFuture != null && learningFuture.isCancelled());
        testNetworkButton.setEnabled(neuralNetwork != null && (learningFuture == null || learningFuture.isDone()));
    }

    private void updateTopInfo() {
        topInfoPanel1.removeAll();
        topInfoPanel2.removeAll();
        if (neuralNetwork == null) {
            var label = new JLabel("Missing neural network");
            label.setForeground(Color.red);
            topInfoPanel1.add(label);
        } else if (inputData == null || outputData == null) {
            var label = new JLabel("Missing learning data");
            label.setForeground(Color.red);
            topInfoPanel1.add(label);
        } else {
            topInfoPanel1.add(new JLabel(dataImportInfo));
            if (learningConfigurationData != null) {
                var crossValidationInfoText = "Cross-validation: %s".formatted(
                        learningConfigurationData.crossValidationGroupsCount()
                                .map("Enabled (%d groups)"::formatted)
                                .orElse("Disabled")
                );
                topInfoPanel1.add(new JLabel(crossValidationInfoText));

                topInfoPanel2.add(new JLabel("Refresh rate: %d epochs".formatted(learningConfigurationData.epochBatchSize())));
                topInfoPanel2.add(new JLabel("Learning rate: %.3f".formatted(learningConfigurationData.learningRate())));
                if (learningConfigurationData.accuracyEndConditionValue().isPresent() ||
                        learningConfigurationData.epochsCountEndConditionValue().isPresent() ||
                        learningConfigurationData.errorEndConditionValue().isPresent() ||
                        learningConfigurationData.timeEndConditionValue().isPresent()) {
                    var endConditionsTexts = new ArrayList<String>();
                    learningConfigurationData.accuracyEndConditionValue()
                            .ifPresent(v -> endConditionsTexts.add("accuracy %.3f".formatted(v)));
                    learningConfigurationData.epochsCountEndConditionValue()
                            .ifPresent(v -> endConditionsTexts.add("epochs %d".formatted(v)));
                    learningConfigurationData.errorEndConditionValue()
                            .ifPresent(v -> endConditionsTexts.add("error %.3f".formatted(v)));
                    learningConfigurationData.timeEndConditionValue()
                            .ifPresent(v -> endConditionsTexts.add("time %ds".formatted(v)));
                    topInfoPanel2.add(new JLabel("End conditions: " + endConditionsTexts));
                }
            }
        }
        topInfoPanel1.revalidate();
        topInfoPanel1.repaint();
        topInfoPanel2.revalidate();
        topInfoPanel2.repaint();
    }

    private void updateBottomInfo(LearningStatisticsProvider stats) {
        if (stats != null) {
            bottomEpochsLabel.setText(String.valueOf(stats.getLearningEpochsCompletedCount()));
            bottomTimeLabel.setText(convertSecondsToTimer(stats.getTotalLearningTimeSeconds()));
            bottomAccuracyLabel.setText("%.3f".formatted(stats.getCurrentAccuracy()));
            bottomErrorLabel.setText("%.3f".formatted(stats.getCurrentError()));

            bottomInfoPanel.setVisible(true);
            bottomInfoPanel.revalidate();
            bottomInfoPanel.repaint();
        } else {
            bottomInfoPanel.setVisible(false);
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

    private String convertSecondsToTimer(long input) {
        var minutes = input / 60;
        var seconds = input % 60;
        if (minutes > 59) {
            var hours = minutes / 60;
            minutes = minutes % 60;
            return "%02d:%02d:%02d".formatted(hours, minutes, seconds);
        } else {
            return "%02d:%02d".formatted(minutes, seconds);
        }
    }

    private class CloseListener extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            if (learningFuture != null && !learningFuture.isDone()) {
                learningFuture.cancel(true);
            }
        }
    }
}
