package com.github.adrian99.neuralnetworkgui.window;

import com.github.adrian99.neuralnetwork.learning.BackPropagationLearningFunction;
import com.github.adrian99.neuralnetwork.learning.endcondition.AccuracyEndCondition;
import com.github.adrian99.neuralnetwork.learning.endcondition.EpochsCountEndCondition;
import com.github.adrian99.neuralnetwork.learning.endcondition.ErrorEndCondition;
import com.github.adrian99.neuralnetwork.learning.endcondition.TimeEndCondition;
import com.github.adrian99.neuralnetwork.learning.error.SumSquaredErrorFunction;
import com.github.adrian99.neuralnetwork.learning.supervisor.LearningSupervisor;
import com.github.adrian99.neuralnetworkgui.data.LearningConfigurationData;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.Consumer;

import static com.github.adrian99.neuralnetworkgui.util.GridBagLayoutCreator.addComponent;
import static com.github.adrian99.neuralnetworkgui.util.GridBagLayoutCreator.addInputWithLabel;

public class LearningConfiguratorWindow extends JDialog {
    private final transient Consumer<LearningConfigurationData> onStart;
    private final JSpinner refreshPerEpochsCountInput;
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
        this.onStart = onStart;

        setTitle("Configure learning process");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setModalityType(ModalityType.APPLICATION_MODAL);

        getContentPane().setLayout(new GridBagLayout());
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        refreshPerEpochsCountInput = new JSpinner(new SpinnerNumberModel(1000, 1, 1000000, 1));
        learningRateInput = new JSpinner(new SpinnerNumberModel(1, -10000, 10000, 0.001));

        crossValidationCheckbox = new JCheckBox("Enable cross-validation", false);
        crossValidationCheckbox.addItemListener(i -> drawForm());
        crossValidationGroupsCountInput = new JSpinner(new SpinnerNumberModel(2, 2, crossValidationGroupsCountMaxLimit, 1));

        accuracyEndConditionCheckbox = new JCheckBox("Enable accuracy end condition");
        accuracyEndConditionCheckbox.addItemListener(i -> drawForm());
        desiredAccuracyInput = new JSpinner(new SpinnerNumberModel(0.8, 0, 1, 0.001));
        epochsCountEndConditionCheckbox = new JCheckBox("Enable epochs count end condition");
        epochsCountEndConditionCheckbox.addItemListener(i -> drawForm());
        desiredEpochsCountInput = new JSpinner(new SpinnerNumberModel(1000, 1, 1000000000, 1));
        errorEndConditionCheckbox = new JCheckBox("Enable error end condition");
        errorEndConditionCheckbox.addItemListener(i -> drawForm());
        desiredErrorInput = new JSpinner(new SpinnerNumberModel(0.2, 0, 1000000, 0.001));
        timeEndConditionCheckbox = new JCheckBox("Enable time end condition");
        timeEndConditionCheckbox.addItemListener(i -> drawForm());
        desiredTimeInput = new JSpinner(new SpinnerNumberModel(60, 1, 10000000, 1));

        drawForm();

        setVisible(true);
    }

    private void drawForm() {
        getContentPane().removeAll();
        var rowIndex = 0;

        addInputWithLabel(getContentPane(), refreshPerEpochsCountInput, "Display refresh rate (epochs)", 0, rowIndex++);

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
        var configuration = new LearningSupervisor.Configuration(
                new SumSquaredErrorFunction(),
                new BackPropagationLearningFunction((Double) learningRateInput.getValue())
        ).setEpochBatchSize((Integer) refreshPerEpochsCountInput.getValue());

        if (accuracyEndConditionCheckbox.isSelected()) {
            configuration.addEndCondition(new AccuracyEndCondition((Double) desiredAccuracyInput.getValue()));
        }
        if (epochsCountEndConditionCheckbox.isSelected()) {
            configuration.addEndCondition(new EpochsCountEndCondition((Integer) desiredEpochsCountInput.getValue()));
        }
        if (errorEndConditionCheckbox.isSelected()) {
            configuration.addEndCondition(new ErrorEndCondition((Double) desiredErrorInput.getValue()));
        }
        if (timeEndConditionCheckbox.isSelected()) {
            configuration.addEndCondition(new TimeEndCondition((Integer) desiredTimeInput.getValue()));
        }

        onStart.accept(new LearningConfigurationData(
                crossValidationCheckbox.isSelected() ?
                        (Integer) crossValidationGroupsCountInput.getValue() :
                        0,
                configuration
        ));
        dispose();
    }
}
