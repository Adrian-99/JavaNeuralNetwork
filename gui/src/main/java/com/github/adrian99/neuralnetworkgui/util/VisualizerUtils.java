package com.github.adrian99.neuralnetworkgui.util;

import java.awt.*;

public class VisualizerUtils {
    private Double weightsLowerBound;
    private Double weightsUpperBound;
    private int scale;

    public Double getWeightsLowerBound() {
        return weightsLowerBound;
    }

    public void setWeightsLowerBound(Double weightsLowerBound) {
        this.weightsLowerBound = weightsLowerBound;
    }

    public Double getWeightsUpperBound() {
        return weightsUpperBound;
    }

    public void setWeightsUpperBound(Double weightsUpperBound) {
        this.weightsUpperBound = weightsUpperBound;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public void drawNetworkInput(Graphics2D graphics, Point position) {
        graphics.setColor(Color.black);
        graphics.fillRect(position.x - 6 * scale, position.y - 6 * scale, 12 * scale, 12 * scale);
    }

    public void drawNeuron(Graphics2D graphics, Point position) {
        graphics.setColor(Color.DARK_GRAY);
        graphics.fillOval(position.x - 6 * scale, position.y - 6 * scale, 12 * scale, 12 * scale);
    }

    public void drawWeight(Graphics2D graphics,
                            Point source,
                            Point destination,
                            double value) {
        drawLine(graphics, source, destination, value, 0, 120, 240);
    }

    public void drawBias(Graphics2D graphics, Point neuronPosition, double value) {
        drawLine(graphics, new Point(neuronPosition.x - 5 * scale, neuronPosition.y - 10 * scale), neuronPosition, value, 0, 240, 120);
    }

    private void drawLine(Graphics2D graphics,
                          Point from,
                          Point to,
                          double value,
                          int baseColorR,
                          int baseColorG,
                          int baseColorB) {
        var valueScale = (value - weightsLowerBound) / (weightsUpperBound - weightsLowerBound);
        graphics.setColor(new Color(
                (int) (baseColorR + (255 - baseColorR) * (1 - valueScale)),
                (int) (baseColorG + (255 - baseColorG) * (1 - valueScale)),
                (int) (baseColorB + (255 - baseColorB) * (1 - valueScale)),
                (int) (255 * valueScale)
        ));
        graphics.setStroke(new BasicStroke((float) (scale * (valueScale + 1))));
        graphics.drawLine(from.x, from.y, to.x, to.y);
    }
}
