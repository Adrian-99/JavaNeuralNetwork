package com.github.adrian99.neuralnetworkgui.util;

import javax.swing.*;
import java.awt.*;

public class GridBagLayoutCreator {
    private final Container container;
    private final JComponent component;
    private final GridBagConstraints constraints;

    private GridBagLayoutCreator(Container container, JComponent component) {
        this.container = container;
        this.component = component;
        this.constraints = new GridBagConstraints();
    }

    public static GridBagLayoutCreator addComponent(Container container, JComponent component) {
        return new GridBagLayoutCreator(container, component);
    }

    public GridBagLayoutCreator gridX(int value) {
        constraints.gridx = value;
        return this;
    }

    public GridBagLayoutCreator gridY(int value) {
        constraints.gridy = value;
        return this;
    }

    public GridBagLayoutCreator gridWidth(int value) {
        constraints.gridwidth = value;
        return this;
    }

    public GridBagLayoutCreator gridHeight(int value) {
        constraints.gridheight = value;
        return this;
    }

    public GridBagLayoutCreator fill(int value) {
        constraints.fill = value;
        return this;
    }

    public void done() {
        container.add(component, constraints);
    }
}
