package com.github.adrian99.neuralnetworkgui.util;

import java.awt.*;

public class VisualizerUtils {
    private Double weightsAbsBound;
    private int scale;

    public Double getWeightsAbsBound() {
        return weightsAbsBound;
    }

    public void setWeightsAbsBound(Double weightsAbsBound) {
        this.weightsAbsBound = weightsAbsBound;
    }

    public int getScale() {
        return scale;
    }

    public boolean setScale(int scale) {
        var scaleChanges = this.scale != scale;
        this.scale = scale;
        return scaleChanges;
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
                           double value,
                           boolean drawValue) {
        drawLine(graphics, source, destination, Math.abs(value), value >= 0 ? new Color(0, 120, 240) : new Color(240, 120, 0));
        if (drawValue) {
            var valuePoint = new Point(source);
            valuePoint.translate((int) ((destination.getX() - source.getX()) * 0.25), (int) ((destination.getY() - source.getY()) * 0.25));
            drawValue(graphics, valuePoint, value);
        }
    }

    public void drawBias(Graphics2D graphics, Point neuronPosition, double value, boolean drawValue) {
        var startPoint = new Point(neuronPosition.x - 5 * scale, neuronPosition.y - 10 * scale);
        drawLine(graphics, startPoint, neuronPosition, Math.abs(value), value >= 0 ? new Color(0, 240, 120) : new Color(240, 180, 0));
        if (drawValue) {
            drawValue(graphics, new Point(startPoint), value);
        }
    }

    private void drawLine(Graphics2D graphics,
                          Point from,
                          Point to,
                          double valueAbs,
                          Color baseColor) {
        var valueScale = valueAbs / weightsAbsBound;
        graphics.setColor(new Color(
                baseColor.getRed(),
                baseColor.getGreen(),
                baseColor.getBlue(),
//                (int) (baseColor.getRed() + (240 - baseColor.getRed()) * (1 - valueScale)),
//                (int) (baseColor.getGreen() + (240 - baseColor.getGreen()) * (1 - valueScale)),
//                (int) (baseColor.getBlue() + (240 - baseColor.getBlue()) * (1 - valueScale)),
//                100 + (int) (155 * valueScale)
                20 + (int) (235 * valueScale)
        ));
        graphics.setStroke(new BasicStroke((float) (scale * (valueScale + 1))));
        graphics.drawLine(from.x, from.y, to.x, to.y);
    }

    private void drawValue(Graphics2D graphics, Point at, double value) {
        at.translate(scale * -3, scale);
        graphics.setColor(Color.black);
        graphics.setFont(new Font(graphics.getFont().getFamily(), Font.PLAIN, scale * 2));
        graphics.drawString("%.3f".formatted(value), (int) at.getX(), (int) at.getY());
    }
}
