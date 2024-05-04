package com.github.adrian99.neuralnetworkgui.window;

import com.github.adrian99.neuralnetworkgui.data.StatisticsRecord;
import com.github.adrian99.neuralnetworkgui.util.StatisticsCollector;
import com.github.adrian99.neuralnetworkgui.util.WindowUtils;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

import static com.github.adrian99.neuralnetworkgui.util.GridBagLayoutCreator.addComponent;
import static com.github.adrian99.neuralnetworkgui.util.GridBagLayoutCreator.addInputWithLabel;

public class PlotCreatorWindow extends JDialog {
    private static final String ACCURACY = "Accuracy";
    private static final String ERROR = "Error";
    private static final String EPOCHS = "Epochs";
    private static final String TIME = "Time [ms]";

    private static final List<String> yAxisValues = List.of(ACCURACY, ERROR, EPOCHS, TIME);
    private final transient StatisticsCollector statisticsCollector;
    private final JComboBox<String> xAxisComboBox;
    private final JSpinner xAxisMinStepInput;
    private final JButton showButton;
    private JComboBox<String> yAxisComboBox;

    public PlotCreatorWindow(StatisticsCollector statisticsCollector) {
        this.statisticsCollector = statisticsCollector;

        setIconImage(WindowUtils.getIconImage());
        setTitle("Learning statistics");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setModalityType(ModalityType.APPLICATION_MODAL);

        getContentPane().setLayout(new GridBagLayout());
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        xAxisComboBox = new JComboBox<>(new String[] { EPOCHS, TIME });
        xAxisComboBox.addItemListener(e -> {
            createYAxisComboBox();
            drawForm();
        });

        createYAxisComboBox();

        xAxisMinStepInput = new JSpinner(new SpinnerNumberModel(1, 1, 1000000, 1));

        showButton = new JButton("Show");
        showButton.addActionListener(e -> onShow());

        drawForm();
        setVisible(true);
    }

    private void createYAxisComboBox() {
        yAxisComboBox = new JComboBox<>(yAxisValues.stream().filter(v -> !v.equals(xAxisComboBox.getSelectedItem())).toArray(String[]::new));

    }

    private void drawForm() {
        getContentPane().removeAll();

        addInputWithLabel(getContentPane(), xAxisComboBox, "X axis", 0, 0);
        addInputWithLabel(getContentPane(), yAxisComboBox, "Y axis", 0, 1);
        addInputWithLabel(getContentPane(), xAxisMinStepInput, "X axis step", 0, 2);
        addComponent(getContentPane(), showButton)
                .gridX(0)
                .gridY(3)
                .gridWidth(2)
                .done();

        pack();
    }

    private void onShow() {
        var xAxis = (String) xAxisComboBox.getSelectedItem();
        var yAxis = (String) yAxisComboBox.getSelectedItem();
        var xAxisMinStep = (Integer) xAxisMinStepInput.getValue();
        if (xAxis != null && yAxis != null && xAxisMinStep != null) {
            var series = new XYSeries(yAxis);
            var previousX = 0.0;
            Double nextYValue = null;
            int nextYCount = 0;
            for (var s : statisticsCollector.getStatistics()) {
                var x = getValue(s, xAxis);
                var y = getValue(s, yAxis);
                if (Double.isFinite(x) && Double.isFinite(y)) {
                    nextYCount++;
                    if (nextYValue == null) {
                        nextYValue = y;
                    } else if (yAxis.equals(ACCURACY) || yAxis.equals(ERROR)) {
                        nextYValue = (nextYValue * (nextYCount - 1) + y) / nextYCount;
                    } else if (y > nextYValue) {
                        nextYValue = y;
                    }
                    if (x - previousX >= xAxisMinStep) {
                        series.add(x, nextYValue);
                        nextYValue = null;
                        nextYCount = 0;
                        previousX = x;
                    }
                }
            }
            new PlotWindow(xAxis, yAxis, new XYSeriesCollection(series));
        }
    }

    private double getValue(StatisticsRecord stats, String valueName) {
        return switch (valueName) {
            case ACCURACY -> stats.accuracy();
            case ERROR -> stats.error();
            case EPOCHS -> stats.epochs();
            case TIME -> stats.timeMillis();
            default -> throw new IllegalStateException("Unexpected value name: " + valueName);
        };
    }
}
