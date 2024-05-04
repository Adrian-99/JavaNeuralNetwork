package com.github.adrian99.neuralnetworkgui.window;

import com.github.adrian99.neuralnetwork.NeuralNetwork;
import com.github.adrian99.neuralnetworkgui.util.WindowUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.github.adrian99.neuralnetworkgui.util.GridBagLayoutCreator.addComponent;
import static com.github.adrian99.neuralnetworkgui.util.GridBagLayoutCreator.addInputWithLabel;

public class NetworkManualTesterWindow extends JDialog {
    private final NeuralNetwork neuralNetwork;
    private final List<JSpinner> networkInputsInputs;
    private final List<JLabel> networkOutputsLabels;
    private boolean areOutputsCalculated = false;

    public NetworkManualTesterWindow(NeuralNetwork neuralNetwork) {
        this.neuralNetwork = neuralNetwork;

        setIconImage(WindowUtils.getIconImage());
        setTitle("Manual test of neural network");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setModalityType(ModalityType.APPLICATION_MODAL);

        getContentPane().setLayout(new GridBagLayout());
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        var networkInputsCount = neuralNetwork.getLayers()[0].getNeurons()[0].getWeights().length;
        var networkOutputsCount = neuralNetwork.getLayers()[neuralNetwork.getLayers().length - 1].getNeurons().length;

        networkInputsInputs = new ArrayList<>();
        for (var i = 0; i < networkInputsCount; i++) {
            var input = new JSpinner(new SpinnerNumberModel(0, -1000000, 1000000, 0.001));
            input.addChangeListener(e -> onInputChange());
            networkInputsInputs.add(input);
            addInputWithLabel(getContentPane(), input, "Input %d".formatted(i + 1), 0, i);
        }

        addComponent(getContentPane(), Box.createRigidArea(new Dimension(0, 10)))
                .gridX(0)
                .gridY(networkInputsCount)
                .gridWidth(2)
                .done();

        var calculateButton = new JButton("Calculate outputs");
        calculateButton.addActionListener(e -> onCalculate());
        addComponent(getContentPane(), calculateButton)
                .gridX(0)
                .gridY(networkInputsCount + 1)
                .gridWidth(2)
                .fill(GridBagConstraints.HORIZONTAL)
                .done();

        addComponent(getContentPane(), Box.createRigidArea(new Dimension(0, 10)))
                .gridX(0)
                .gridY(networkInputsCount + 2)
                .gridWidth(2)
                .done();

        networkOutputsLabels = new ArrayList<>();
        for (var i = 0; i < networkOutputsCount; i++) {
            var label = new JLabel("Output %d".formatted(i + 1));
            addComponent(getContentPane(), label)
                    .gridX(0)
                    .gridY(networkInputsCount + 3 + i)
                    .fill(GridBagConstraints.HORIZONTAL)
                    .done();

            var valueLabel = new JLabel(" ");
            valueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            valueLabel.setBorder(new LineBorder(Color.GRAY));
            addComponent(getContentPane(), valueLabel)
                    .gridX(1)
                    .gridY(networkInputsCount + 3 + i)
                    .fill(GridBagConstraints.HORIZONTAL)
                    .done();
            networkOutputsLabels.add(valueLabel);
        }

        pack();
        setVisible(true);
    }

    private void onInputChange() {
        if (areOutputsCalculated) {
            networkOutputsLabels.forEach(label -> label.setText(" "));
            revalidate();
            repaint();
            areOutputsCalculated = false;
        }
    }

    private void onCalculate() {
        var inputs = networkInputsInputs.stream()
                .map(JSpinner::getValue)
                .mapToDouble(o -> (double) o)
                .toArray();
        var outputs = neuralNetwork.activate(inputs);
        for (var i = 0; i < outputs.length; i++) {
            networkOutputsLabels.get(i).setText("%.3f".formatted(outputs[i]));
        }
    }
}
