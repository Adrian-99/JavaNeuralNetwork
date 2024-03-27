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
    private static final Map<String, Map<String, Double>> ACTIVATION_FUNCTION_PROPERTIES;
    private static final String[] WEIGHT_INITIALIZATION_FUNCTIONS;

    static {
        ACTIVATION_FUNCTION_PROPERTIES = new LinkedHashMap<>();
        var linearFunctionProperties = new LinkedHashMap<String, Double>();
        linearFunctionProperties.put("Slope", 1d);
        linearFunctionProperties.put("Intercept", 0d);
        ACTIVATION_FUNCTION_PROPERTIES.put(LinearActivationFunction.class.getSimpleName(), linearFunctionProperties);
        var logisticFunctionProperties = new LinkedHashMap<String, Double>();
        logisticFunctionProperties.put("Growth rate", 1d);
        logisticFunctionProperties.put("Supremum", 1d);
        ACTIVATION_FUNCTION_PROPERTIES.put(LogisticActivationFunction.class.getSimpleName(), logisticFunctionProperties);
        ACTIVATION_FUNCTION_PROPERTIES.put(UnitStepActivationFunction.class.getSimpleName(), Collections.emptyMap());

        WEIGHT_INITIALIZATION_FUNCTIONS = new String[] {
                XavierWeightInitializationFunction.class.getSimpleName(),
                NormalizedXavierWeightInitializationFunction.class.getSimpleName()
        };
    }

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

    private void initializeActivationFunctionParametersInputs(Collection<Double> values) {
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
                initializeActivationFunctionParametersInputs(activationFunctionParameters.values());
                var i = 0;
                for (var parameter : activationFunctionParameters.keySet()) {
                    var label = new JLabel(parameter);
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
                    i++;
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
