package com.github.adrian99.neuralnetworkgui.window;

import com.github.adrian99.neuralnetworkgui.data.NetworkData;
import com.github.adrian99.neuralnetworkgui.data.NetworkLayerData;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.github.adrian99.neuralnetworkgui.util.GridBagLayoutCreator.addComponent;

public class NetworkCreatorWindow extends JDialog {
    private final JSpinner networkInputsCountInput;
    private final JPanel networkLayersPanel;
    private final JButton doneButton;
    private final List<NetworkLayerData> networkLayersData = new ArrayList<>();
    private final transient Consumer<NetworkData> onDone;

    public NetworkCreatorWindow(Consumer<NetworkData> onDone) {
        this.onDone = onDone;

        setTitle("Create new neural network");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setModalityType(ModalityType.APPLICATION_MODAL);
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        var topPanel = new JPanel(new FlowLayout());
        getContentPane().add(topPanel);
        var networkInputsCountLabel = new JLabel("Inputs count");
        networkInputsCountInput = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
        networkInputsCountLabel.setLabelFor(networkInputsCountInput);
        topPanel.add(networkInputsCountLabel);
        topPanel.add(networkInputsCountInput);

        getContentPane().add(Box.createRigidArea(new Dimension(0, 10)));

        networkLayersPanel = new JPanel();
        networkLayersPanel.setLayout(new GridBagLayout());
        getContentPane().add(networkLayersPanel);

        getContentPane().add(Box.createRigidArea(new Dimension(0, 10)));

        drawNetworkLayersInfo();

        getContentPane().add(Box.createRigidArea(new Dimension(0, 10)));

        var newLayerButton = new JButton("New layer");
        newLayerButton.addActionListener(event -> onNewLayer());
        newLayerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        getContentPane().add(newLayerButton);

        getContentPane().add(Box.createRigidArea(new Dimension(0, 10)));

        doneButton = new JButton("Done");
        doneButton.addActionListener(event -> onDone());
        doneButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        getContentPane().add(doneButton);
        updateDoneButton();

        pack();
        setVisible(true);
    }

    private void drawNetworkLayersInfo() {
        networkLayersPanel.removeAll();
        for (var i = 0; i < networkLayersData.size(); i++) {
            final var layerIndex = i;
            var labelText = String.format(
                    "Layer %d: %d x %s(%s), %s",
                    i + 1,
                    networkLayersData.get(i).neuronsCount(),
                    networkLayersData.get(i).activationFunctionClass(),
                    String.join(
                            ", ",
                            networkLayersData.get(i).activationFunctionParameters().stream()
                                    .map(Object::toString)
                                    .toArray(String[]::new)
                    ),
                    networkLayersData.get(i).weightInitializationFunctionClass()
            );
            addComponent(networkLayersPanel, new JLabel(labelText))
                    .gridX(0)
                    .gridY(i)
                    .gridWidth(2)
                    .done();

            var editButton = new JButton("Edit");
            editButton.addActionListener(event -> onLayerEdit(layerIndex));
            addComponent(networkLayersPanel, editButton)
                    .gridX(2)
                    .gridY(i)
                    .done();

            var deleteButton = new JButton("Delete");
            deleteButton.addActionListener(event -> onLayerDelete(layerIndex));
            addComponent(networkLayersPanel, deleteButton)
                    .gridX(3)
                    .gridY(i)
                    .done();
        }
    }

    private void onLayerEdit(int index) {
        new NetworkLayerCreatorWindow(
                networkLayersData.get(index),
                networkLayerData -> {
                    networkLayersData.remove(index);
                    networkLayersData.add(index, networkLayerData);
                    drawNetworkLayersInfo();
                    pack();
                }
        );
    }

    private void onLayerDelete(int index) {
        networkLayersData.remove(index);
        drawNetworkLayersInfo();
        updateDoneButton();
        pack();
    }

    private void onNewLayer() {
        new NetworkLayerCreatorWindow(networkLayerData -> {
            networkLayersData.add(networkLayerData);
            drawNetworkLayersInfo();
            updateDoneButton();
            pack();
        });
    }

    private void onDone() {
        onDone.accept(new NetworkData(
                (Integer) networkInputsCountInput.getValue(),
                networkLayersData
        ));
        dispose();
    }

    private void updateDoneButton() {
        if (networkLayersData.isEmpty()) {
            doneButton.setEnabled(false);
            doneButton.setToolTipText("Network must have at least one layer");
        } else {
            doneButton.setEnabled(true);
            doneButton.setToolTipText(null);
        }
    }
}
