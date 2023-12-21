package com.github.adrian99.neuralnetworkgui.window;

import com.github.adrian99.neuralnetworkgui.data.NetworkLayerData;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.github.adrian99.neuralnetworkgui.util.GridBagLayoutCreator.addComponent;

public class NetworkCreatorWindow extends JFrame {
    private final JPanel networkLayersPanel;
    private final List<NetworkLayerData> networkLayersData = new ArrayList<>();

    public NetworkCreatorWindow() {
        setTitle("Create new neural network");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

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

        var doneButton = new JButton("Done");
        doneButton.addActionListener(event -> onDone());
        doneButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        getContentPane().add(doneButton);

        getContentPane().add(Box.createRigidArea(new Dimension(0, 10)));

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
        pack();
    }

    private void onNewLayer() {
        new NetworkLayerCreatorWindow(networkLayerData -> {
            networkLayersData.add(networkLayerData);
            drawNetworkLayersInfo();
            pack();
        });
    }

    private void onDone() {
    }
}
