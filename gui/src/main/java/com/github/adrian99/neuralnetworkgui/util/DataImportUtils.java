package com.github.adrian99.neuralnetworkgui.util;

import com.github.adrian99.neuralnetwork.NeuralNetwork;
import com.github.adrian99.neuralnetwork.data.csv.CsvDataLoader;
import com.github.adrian99.neuralnetwork.learning.data.InputsAndTargets;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.function.BiConsumer;

public class DataImportUtils {
    private DataImportUtils() {}

    public static boolean importFromFile(JFileChooser fileChooser,
                                         Component parent,
                                         NeuralNetwork neuralNetwork,
                                         BiConsumer<File, InputsAndTargets> resultConsumer) {
        if (fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            var file = fileChooser.getSelectedFile();
            try {
                var numericData = new CsvDataLoader(file).toNumericData();
                var networkInputsCount = neuralNetwork.getLayers()[0].getNeurons()[0].getWeights().length;
                var networkOutputsCount = neuralNetwork.getLayers()[neuralNetwork.getLayers().length - 1].getNeurons().length;
                if (numericData.getColumnsCount() >= networkInputsCount + networkOutputsCount) {
                    resultConsumer.accept(
                            file,
                            new InputsAndTargets(
                                    numericData.toDoubleArray(0, networkInputsCount - 1),
                                    numericData.toIntArray(networkInputsCount, networkInputsCount + networkOutputsCount - 1)
                            )
                    );
                    return true;
                } else {
                    JOptionPane.showMessageDialog(
                            parent,
                            "Not enough columns of data:\n" +
                                    "Neural network has " + networkInputsCount + " inputs and " + networkOutputsCount + " outputs\n" +
                                    "Expected at least " + (networkInputsCount + networkOutputsCount) + " columns of data, got " + numericData.getColumnsCount(),
                            "Incorrect data error",
                            JOptionPane.WARNING_MESSAGE
                    );
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        parent,
                        "Error occurred while importing data from file:\n" + e.getMessage(),
                        "Importing error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
        return false;
    }
}
