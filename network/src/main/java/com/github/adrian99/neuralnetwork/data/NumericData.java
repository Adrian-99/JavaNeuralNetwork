package com.github.adrian99.neuralnetwork.data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NumericData {
    private final List<List<Double>> columns;

    public NumericData(List<List<Double>> columns) {
        this.columns = columns;
    }

    public NumericData normalize(int columnIndex) {
        return normalize(columnIndex, -1, 1);
    }

    public NumericData normalize(int columnIndex, double targetMin, double targetMax) {
        var column = columns.get(columnIndex);
        if (!column.isEmpty()) {
            var min = column.get(0);
            var max = min;
            for (var value : column) {
                if (value < min) {
                    min = value;
                }
                if (value > max) {
                    max = value;
                }
            }
            var diff = max - min;
            if (diff > 0) {
                var finalMin = min;
                var targetDiff = targetMax - targetMin;
                columns.set(
                        columnIndex,
                        columns.get(columnIndex)
                                .stream()
                                .map(value -> (value - finalMin) / diff * targetDiff + targetMin)
                                .collect(Collectors.toCollection(ArrayList::new))
                );
            } else {
                columns.set(
                        columnIndex,
                        columns.get(columnIndex)
                                .stream()
                                .map(value -> 0.0)
                                .collect(Collectors.toCollection(ArrayList::new))
                );
            }
        }
        return this;
    }

    public double[][] toArray(int startColumnIndex, int endColumnIndex) {
        var result = new double[columns.get(startColumnIndex).size()][];
        for (var i = 0; i < columns.get(startColumnIndex).size(); i++) {
            result[i] = new double[endColumnIndex - startColumnIndex + 1];
            for (var j = startColumnIndex; j <= endColumnIndex; j++) {
                result[i][j - startColumnIndex] = columns.get(j).get(i);
            }
        }
        return result;
    }
}
