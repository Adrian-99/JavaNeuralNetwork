package com.github.adrian99.neuralnetworkgui.window;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.data.xy.XYDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class PlotWindow extends JDialog {
    public PlotWindow(String xAxisValue, String yAxisValue, XYDataset data) {
        setTitle("Statistics plot - " + yAxisValue + " by " + xAxisValue);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setModalityType(ModalityType.APPLICATION_MODAL);

        ((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        var plot = ChartFactory.createXYLineChart(yAxisValue + " by " + xAxisValue, xAxisValue, yAxisValue, data);
        getContentPane().add(new ChartPanel(plot));

        pack();
        setVisible(true);
    }
}
