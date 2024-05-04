package com.github.adrian99.neuralnetworkgui.window;

import com.github.adrian99.neuralnetworkgui.util.WindowUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.data.xy.XYDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class PlotWindow extends JDialog {
    public PlotWindow(String xAxisValue, String yAxisValue, XYDataset dataset) {
        this(null, xAxisValue, yAxisValue, dataset);
    }

    public PlotWindow(String title, String xAxisValue, String yAxisValue, XYDataset data) {
        setIconImage(WindowUtils.getIconImage());
        setTitle(title != null ? title : "Statistics plot - " + yAxisValue + " by " + xAxisValue);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setModalityType(ModalityType.APPLICATION_MODAL);

        ((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        var plot = ChartFactory.createXYLineChart(title != null ? title : yAxisValue + " by " + xAxisValue, xAxisValue, yAxisValue, data);
        getContentPane().add(new ChartPanel(plot));

        pack();
        setVisible(true);
    }
}
