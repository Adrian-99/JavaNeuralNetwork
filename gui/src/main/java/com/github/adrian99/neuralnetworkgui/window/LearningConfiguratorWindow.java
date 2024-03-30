package com.github.adrian99.neuralnetworkgui.window;

import com.github.adrian99.neuralnetworkgui.data.LearningConfigurationData;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Optional;
import java.util.function.Consumer;

import static com.github.adrian99.neuralnetworkgui.util.GridBagLayoutCreator.addComponent;
import static com.github.adrian99.neuralnetworkgui.util.GridBagLayoutCreator.addInputWithLabel;

public class LearningConfiguratorWindow extends JDialog {
    private final transient Consumer<LearningConfigurationData> onStart;
    private final JSpinner displayRefreshRateInput;
    private final JSpinner epochsBatchSizeInput;
    private final JSpinner learningRateInput;
    private final JCheckBox crossValidationCheckbox;
    private final JSpinner crossValidationGroupsCountInput;
    private final JCheckBox accuracyEndConditionCheckbox;
    private final JSpinner desiredAccuracyInput;
    private final JCheckBox epochsCountEndConditionCheckbox;
    private final JSpinner desiredEpochsCountInput;
    private final JCheckBox errorEndConditionCheckbox;
    private final JSpinner desiredErrorInput;
    private final JCheckBox timeEndConditionCheckbox;
    private final JSpinner desiredTimeInput;

    public LearningConfiguratorWindow(Consumer<LearningConfigurationData> onStart, int crossValidationGroupsCountMaxLimit) {
        this(new LearningConfigurationData(), onStart, crossValidationGroupsCountMaxLimit);
    }

    public LearningConfiguratorWindow(LearningConfigurationData configuration,
                                      Consumer<LearningConfigurationData> onStart,
                                      int crossValidationGroupsCountMaxLimit) {
        this.onStart = onStart;

        setTitle("Configure learning process");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setModalityType(ModalityType.APPLICATION_MODAL);

        getContentPane().setLayout(new GridBagLayout());
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        displayRefreshRateInput = new JSpinner(new SpinnerNumberModel(configuration.displayRefreshRate(), 0.1, 60, 0.1));
        epochsBatchSizeInput = new JSpinner(new SpinnerNumberModel(configuration.epochBatchSize(), 1, 1000000, 1));
        learningRateInput = new JSpinner(new SpinnerNumberModel(configuration.learningRate(), -10000, 10000, 0.001));

        crossValidationCheckbox = new JCheckBox("Enable cross-validation", configuration.crossValidationGroupsCount().isPresent());
        crossValidationCheckbox.addItemListener(i -> drawForm());
        crossValidationGroupsCountInput = new JSpinner(new SpinnerNumberModel(configuration.crossValidationGroupsCount().orElse(2).intValue(), 2, crossValidationGroupsCountMaxLimit, 1));

        accuracyEndConditionCheckbox = new JCheckBox("Enable accuracy end condition", configuration.accuracyEndConditionValue().isPresent());
        accuracyEndConditionCheckbox.addItemListener(i -> drawForm());
        desiredAccuracyInput = new JSpinner(new SpinnerNumberModel(configuration.accuracyEndConditionValue().orElse(0.8).doubleValue(), 0, 1, 0.001));
        epochsCountEndConditionCheckbox = new JCheckBox("Enable epochs count end condition", configuration.epochsCountEndConditionValue().isPresent());
        epochsCountEndConditionCheckbox.addItemListener(i -> drawForm());
        desiredEpochsCountInput = new JSpinner(new SpinnerNumberModel(configuration.epochsCountEndConditionValue().orElse(1000).intValue(), 1, 1000000000, 1));
        errorEndConditionCheckbox = new JCheckBox("Enable error end condition", configuration.errorEndConditionValue().isPresent());
        errorEndConditionCheckbox.addItemListener(i -> drawForm());
        desiredErrorInput = new JSpinner(new SpinnerNumberModel(configuration.errorEndConditionValue().orElse(0.2).doubleValue(), 0, 1000000, 0.001));
        timeEndConditionCheckbox = new JCheckBox("Enable time end condition", configuration.timeEndConditionValue().isPresent());
        timeEndConditionCheckbox.addItemListener(i -> drawForm());
        desiredTimeInput = new JSpinner(new SpinnerNumberModel(configuration.timeEndConditionValue().orElse(60).intValue(), 1, 10000000, 1));

        drawForm();

        setVisible(true);
    }

    private void drawForm() {
        getContentPane().removeAll();
        var rowIndex = 0;

        addInputWithLabel(getContentPane(), displayRefreshRateInput, "Display refresh rate (seconds)", 0, rowIndex++);

        addInputWithLabel(getContentPane(), epochsBatchSizeInput, "Network epochs batch size", 0, rowIndex++);

        addInputWithLabel(getContentPane(), learningRateInput, "Learning rate", 0, rowIndex++);

        rowIndex = addCheckboxAndInput(
                crossValidationCheckbox,
                crossValidationGroupsCountInput,
                "Groups count",
                rowIndex
        );

        rowIndex = addCheckboxAndInput(
                accuracyEndConditionCheckbox,
                desiredAccuracyInput,
                "Desired accuracy",
                rowIndex
        );

        rowIndex = addCheckboxAndInput(
                epochsCountEndConditionCheckbox,
                desiredEpochsCountInput,
                "Max epochs count",
                rowIndex
        );

        rowIndex = addCheckboxAndInput(
                errorEndConditionCheckbox,
                desiredErrorInput,
                "Desired error",
                rowIndex
        );

        rowIndex = addCheckboxAndInput(
                timeEndConditionCheckbox,
                desiredTimeInput,
                "Max time (seconds)",
                rowIndex
        );

        var startButton = new JButton("Start");
        startButton.addActionListener(event -> onStart());
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addComponent(getContentPane(), startButton)
                .gridX(0)
                .gridY(rowIndex)
                .gridWidth(2)
                .done();

        pack();
    }

    private int addCheckboxAndInput(JCheckBox checkBox, JSpinner input, String labelText, int rowIndex) {
        addComponent(getContentPane(), checkBox)
                .gridX(0)
                .gridY(rowIndex++)
                .gridWidth(2)
                .fill(GridBagConstraints.HORIZONTAL)
                .done();

        if (checkBox.isSelected()) {
            addInputWithLabel(getContentPane(), input, labelText, 0, rowIndex++);
        }

        return rowIndex;
    }

    private void onStart() {
        onStart.accept(new LearningConfigurationData(
                (Double) displayRefreshRateInput.getValue(),
                (Integer) epochsBatchSizeInput.getValue(),
                (Double) learningRateInput.getValue(),
                crossValidationCheckbox.isSelected() ? Optional.of((Integer) crossValidationGroupsCountInput.getValue()) : Optional.empty(),
                accuracyEndConditionCheckbox.isSelected() ? Optional.of((Double) desiredAccuracyInput.getValue()) : Optional.empty(),
                epochsCountEndConditionCheckbox.isSelected() ? Optional.of((Integer) desiredEpochsCountInput.getValue()) : Optional.empty(),
                errorEndConditionCheckbox.isSelected() ? Optional.of((Double) desiredErrorInput.getValue()) : Optional.empty(),
                timeEndConditionCheckbox.isSelected() ? Optional.of((Integer) desiredTimeInput.getValue()) : Optional.empty()
        ));
        dispose();
    }
}
