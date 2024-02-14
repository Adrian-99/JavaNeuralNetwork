package com.github.adrian99.neuralnetworkgui.window;

import com.github.adrian99.neuralnetwork.layer.neuron.activation.LinearActivationFunction;
import com.github.adrian99.neuralnetwork.layer.neuron.activation.LogisticActivationFunction;
import com.github.adrian99.neuralnetwork.layer.neuron.activation.UnitStepActivationFunction;
import com.github.adrian99.neuralnetwork.layer.neuron.weightinitialization.NormalizedXavierWeightInitializationFunction;
import com.github.adrian99.neuralnetwork.layer.neuron.weightinitialization.XavierWeightInitializationFunction;
import com.github.adrian99.neuralnetworkgui.data.NetworkLayerData;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

import static com.github.adrian99.neuralnetworkgui.util.GridBagLayoutCreator.addComponent;

public class NetworkLayerCreatorWindow extends JDialog {
    private static final Map<String, List<String>> ACTIVATION_FUNCTION_PROPERTIES = Map.of(
            LinearActivationFunction.class.getSimpleName(), List.of("Slope", "Intercept"),
            LogisticActivationFunction.class.getSimpleName(), List.of("Growth rate", "Supremum"),
            UnitStepActivationFunction.class.getSimpleName(), Collections.emptyList()
    );
    private static final String[] WEIGHT_INITIALIZATION_FUNCTIONS = new String[] {
            XavierWeightInitializationFunction.class.getSimpleName(),
            NormalizedXavierWeightInitializationFunction.class.getSimpleName()
    };

    private final transient Consumer<NetworkLayerData> onSave;
    private final JSpinner neuronsCountInput;
    private final JComboBox<String> activationFunctionInput;
    private final List<JSpinner> activationFunctionParametersInputs;
    private final JComboBox<String> weightInitializationFunctionInput;

    public NetworkLayerCreatorWindow(Consumer<NetworkLayerData> onSave) {
        this(
                new NetworkLayerData(
                    1,
                    LinearActivationFunction.class.getSimpleName(),
                    List.of(1.0, 0.0),
                    NormalizedXavierWeightInitializationFunction.class.getSimpleName()
                ),
                onSave
        );
    }

    public NetworkLayerCreatorWindow(NetworkLayerData networkLayerData, Consumer<NetworkLayerData> onSave) {
        this.onSave = onSave;

        setTitle("Create new neural network layer");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setModalityType(ModalityType.APPLICATION_MODAL);

        getContentPane().setLayout(new GridBagLayout());
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        addComponent(getContentPane(), new JLabel("Neurons count"))
                .gridX(0)
                .gridY(0)
                .done();

        neuronsCountInput = new JSpinner(new SpinnerNumberModel(networkLayerData.neuronsCount(), 1, 20, 1));

        activationFunctionInput = new JComboBox<>(ACTIVATION_FUNCTION_PROPERTIES.keySet().toArray(String[]::new));
        activationFunctionInput.setSelectedItem(networkLayerData.activationFunctionClass());
        activationFunctionInput.addActionListener(event -> drawForm());

        activationFunctionParametersInputs = new ArrayList<>();
        initializeActivationFunctionParametersInputs(networkLayerData.activationFunctionParameters());

        weightInitializationFunctionInput = new JComboBox<>(WEIGHT_INITIALIZATION_FUNCTIONS);
        weightInitializationFunctionInput.setSelectedItem(networkLayerData.weightInitializationFunctionClass());

        drawForm();

        setVisible(true);
    }

    private void initializeActivationFunctionParametersInputs(int count) {
        var values = new ArrayList<Double>();
        for (var i = 0; i < count; i++) {
            values.add(1.0);
        }
        initializeActivationFunctionParametersInputs(values);
    }

    private void initializeActivationFunctionParametersInputs(List<Double> values) {
        activationFunctionParametersInputs.clear();
        for (var value : values) {
            activationFunctionParametersInputs.add(new JSpinner(new SpinnerNumberModel(value.doubleValue(), -100000, 100000, 0.001)));
        }
    }

    private void drawForm() {
        var rowIndex = 0;

        getContentPane().removeAll();

        var neuronsCountInputLabel = new JLabel("Neurons count");
        neuronsCountInputLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        addComponent(getContentPane(), neuronsCountInputLabel)
                .gridX(0)
                .gridY(rowIndex)
                .fill(GridBagConstraints.HORIZONTAL)
                .done();

        addComponent(getContentPane(), neuronsCountInput)
                .gridX(1)
                .gridY(rowIndex++)
                .fill(GridBagConstraints.HORIZONTAL)
                .done();

        var activationFunctionInputLabel = new JLabel("Activation function");
        activationFunctionInputLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        addComponent(getContentPane(), activationFunctionInputLabel)
                .gridX(0)
                .gridY(rowIndex)
                .fill(GridBagConstraints.HORIZONTAL)
                .done();

        addComponent(getContentPane(), activationFunctionInput)
                .gridX(1)
                .gridY(rowIndex++)
                .fill(GridBagConstraints.HORIZONTAL)
                .done();

        var activationFunctionParameters = ACTIVATION_FUNCTION_PROPERTIES.get(
                Objects.requireNonNull(activationFunctionInput.getSelectedItem()).toString()
        );
        if (activationFunctionParameters != null) {
            if (!activationFunctionParameters.isEmpty()) {
                if (activationFunctionParametersInputs.size() < activationFunctionParameters.size()) {
                    initializeActivationFunctionParametersInputs(activationFunctionParameters.size());
                }
                for (var i = 0; i < activationFunctionParameters.size(); i++) {
                    var label = new JLabel(activationFunctionParameters.get(i));
                    label.setHorizontalAlignment(SwingConstants.RIGHT);
                    addComponent(getContentPane(), label)
                            .gridX(0)
                            .gridY(rowIndex)
                            .fill(GridBagConstraints.HORIZONTAL)
                            .done();

                    addComponent(getContentPane(), activationFunctionParametersInputs.get(i))
                            .gridX(1)
                            .gridY(rowIndex++)
                            .fill(GridBagConstraints.HORIZONTAL)
                            .done();
                }
            } else if (!activationFunctionParametersInputs.isEmpty()) {
                activationFunctionParametersInputs.clear();
            }
        }

        var weightInitializationFunctionInputLabel = new JLabel("Weight initialization function");
        weightInitializationFunctionInputLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        addComponent(getContentPane(), weightInitializationFunctionInputLabel)
                .gridX(0)
                .gridY(rowIndex)
                .fill(GridBagConstraints.HORIZONTAL)
                .done();

        addComponent(getContentPane(), weightInitializationFunctionInput)
                .gridX(1)
                .gridY(rowIndex++)
                .fill(GridBagConstraints.HORIZONTAL)
                .done();

        var saveButton = new JButton("Save");
        saveButton.addActionListener(event -> onSave());
        saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addComponent(getContentPane(), saveButton)
                .gridX(0)
                .gridY(rowIndex)
                .gridWidth(2)
                .done();

        pack();
    }

    private void onSave() {
        onSave.accept(new NetworkLayerData(
                (Integer) neuronsCountInput.getValue(),
                (String) activationFunctionInput.getSelectedItem(),
                activationFunctionParametersInputs.stream()
                        .map(input -> (double) input.getValue())
                        .toList(),
                (String) weightInitializationFunctionInput.getSelectedItem()
        ));
        dispose();
    }
}
