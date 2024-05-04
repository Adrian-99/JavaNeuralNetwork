package com.github.adrian99.neuralnetworkgui.window;

import com.github.adrian99.neuralnetwork.NeuralNetwork;
import com.github.adrian99.neuralnetwork.learning.data.InputsAndTargets;
import com.github.adrian99.neuralnetwork.learning.error.ErrorFunction;
import com.github.adrian99.neuralnetwork.util.Statistics;
import com.github.adrian99.neuralnetworkgui.util.WindowUtils;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Arrays;

import static com.github.adrian99.neuralnetworkgui.util.GridBagLayoutCreator.addComponent;
import static com.github.adrian99.neuralnetworkgui.util.GridBagLayoutCreator.addInputWithLabel;

public class NetworkAutomaticTesterWindow extends JDialog {
    private final int[][] targetOutputs;
    private final double[][] networkOutputs;

    public NetworkAutomaticTesterWindow(NeuralNetwork neuralNetwork, InputsAndTargets data, ErrorFunction errorFunction) {
        targetOutputs = data.getTargets();
        networkOutputs = neuralNetwork.activate(data.getInputs());

        var accuracy = Statistics.accuracy(networkOutputs, targetOutputs);
        var error = Statistics.error(networkOutputs, targetOutputs, errorFunction);

        setIconImage(WindowUtils.getIconImage());
        setTitle("Automatic test of neural network");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setModalityType(ModalityType.APPLICATION_MODAL);

        getContentPane().setLayout(new GridBagLayout());
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        var recordsCountLabel = new JLabel(String.valueOf(data.getInputs().length));
        recordsCountLabel.setBorder(new EmptyBorder(0, 10, 0, 0));
        addInputWithLabel(getContentPane(), recordsCountLabel, "Data records count:", 0, 0);

        var accuracyLabel = new JLabel(String.valueOf(accuracy));
        accuracyLabel.setBorder(new EmptyBorder(0, 10, 0, 0));
        addInputWithLabel(getContentPane(), accuracyLabel, "Accuracy:", 0, 1);

        var errorLabel = new JLabel(String.valueOf(error));
        errorLabel.setBorder(new EmptyBorder(0, 10, 0, 0));
        addInputWithLabel(getContentPane(), errorLabel, "Error:", 0, 2);

        for (var i = 0; i < networkOutputs[0].length; i++) {
            var finalI = i;
            var showOutputsPlotButton = new JButton("Show plot for output " + (i + 1));
            showOutputsPlotButton.addActionListener(e -> onShowOutputsPlot(finalI));
            addComponent(getContentPane(), showOutputsPlotButton)
                    .gridX(0)
                    .gridY(3 + i)
                    .gridWidth(2)
                    .done();
        }

        pack();
        setVisible(true);
    }

    private void onShowOutputsPlot(int outputIndex) {
        var networkOutputsCopy = Arrays.stream(networkOutputs).mapToDouble(outputs -> outputs[outputIndex]).toArray();
        var targetOutputsCopy = Arrays.stream(targetOutputs).mapToInt(outputs -> outputs[outputIndex]).toArray();

        for (var i = 0; i < targetOutputsCopy.length - 1; i++) {
            for (var j = i + 1; j < targetOutputsCopy.length; j++) {
                if (targetOutputsCopy[i] > targetOutputsCopy[j]) {
                    var temp1 = targetOutputsCopy[i];
                    targetOutputsCopy[i] = targetOutputsCopy[j];
                    targetOutputsCopy[j] = temp1;

                    var temp2 = networkOutputsCopy[i];
                    networkOutputsCopy[i] = networkOutputsCopy[j];
                    networkOutputsCopy[j] = temp2;
                }
            }
        }

        var seriesCollection = createSeriesCollection(targetOutputsCopy, networkOutputsCopy);

        new PlotWindow(
                "Network output " + (outputIndex + 1) + " values",
                "Data record",
                "Value",
                seriesCollection
        );
    }

    private XYSeriesCollection createSeriesCollection(int[] targetOutputsSorted, double[] networkOutputsSorted) {
        var targetOutputsSeries = new XYSeries("Target outputs");
        for (var i = 0; i < targetOutputsSorted.length; i++) {
            targetOutputsSeries.add(i + 1.0, targetOutputsSorted[i]);
        }

        var networkOutputsSeries = new XYSeries("Network outputs");
        for (var i = 0; i < networkOutputsSorted.length; i++) {
            networkOutputsSeries.add(i + 1.0, networkOutputsSorted[i]);
        }

        var seriesCollection = new XYSeriesCollection(targetOutputsSeries);
        seriesCollection.addSeries(networkOutputsSeries);

        seriesCollection.setIntervalPositionFactor(0.5);
        return seriesCollection;
    }
}
