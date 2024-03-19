package com.github.adrian99.neuralnetworkgui.window;

import com.github.adrian99.neuralnetworkgui.data.DataConfiguration;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.Consumer;

import static com.github.adrian99.neuralnetworkgui.util.GridBagLayoutCreator.addComponent;

public class DataConfiguratorWindow extends JDialog {
    private final transient Consumer<DataConfiguration> onSave;
    private final JCheckBox crossValidationCheckbox;
    private final JSpinner crossValidationGroupsCountInput;

    public DataConfiguratorWindow(Consumer<DataConfiguration> onSave, DataConfiguration dataConfiguration) {
        this.onSave = onSave;

        setTitle("Configure data for neural network learning");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setModalityType(ModalityType.APPLICATION_MODAL);

        getContentPane().setLayout(new GridBagLayout());
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        crossValidationCheckbox = new JCheckBox("Enable cross-validation", dataConfiguration.crossValidationEnabled());
        crossValidationCheckbox.addItemListener(i -> drawForm());

        crossValidationGroupsCountInput = new JSpinner(new SpinnerNumberModel(dataConfiguration.crossValidationGroupsCount(), 1, 1000, 1));

        drawForm();

        setVisible(true);
    }

    private void drawForm() {
        getContentPane().removeAll();

        addComponent(getContentPane(), crossValidationCheckbox)
                .gridX(0)
                .gridY(0)
                .gridWidth(2)
                .fill(GridBagConstraints.HORIZONTAL)
                .done();

        if (crossValidationCheckbox.isSelected()) {
            var crossValidationGroupsCountInputLabel = new JLabel("Groups count");
            crossValidationGroupsCountInputLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            addComponent(getContentPane(), crossValidationGroupsCountInputLabel)
                    .gridX(0)
                    .gridY(1)
                    .fill(GridBagConstraints.HORIZONTAL)
                    .done();

            addComponent(getContentPane(), crossValidationGroupsCountInput)
                    .gridX(1)
                    .gridY(1)
                    .fill(GridBagConstraints.HORIZONTAL)
                    .done();
        }

        var saveButton = new JButton("Save");
        saveButton.addActionListener(event -> onSave());
        saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addComponent(getContentPane(), saveButton)
                .gridX(0)
                .gridY(crossValidationCheckbox.isEnabled() ? 2 : 1)
                .gridWidth(2)
                .done();

        pack();
    }

    private void onSave() {
        onSave.accept(new DataConfiguration(
                crossValidationCheckbox.isSelected(),
                (Integer) crossValidationGroupsCountInput.getValue()
        ));
        dispose();
    }
}
