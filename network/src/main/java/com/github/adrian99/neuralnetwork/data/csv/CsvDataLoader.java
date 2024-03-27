package com.github.adrian99.neuralnetwork.data.csv;

import com.github.adrian99.neuralnetwork.data.NumericData;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CsvDataLoader {
    private static final String DELIMITER = ",";

    private final List<List<String>> columns;

    public CsvDataLoader(File file) throws IOException {
        try (var bufferedReader = new BufferedReader(new FileReader(file))) {
            columns = new ArrayList<>();
            String line;
            for (var lineNumber = 1; (line = bufferedReader.readLine()) != null; lineNumber++) {
                if (line.isEmpty()) {
                    continue;
                }
                var values = line.split(DELIMITER);
                if (columns.isEmpty()) {
                    for (var i = 0; i < values.length; i++) {
                        columns.add(new ArrayList<>());
                    }
                }
                if (columns.size() == values.length) {
                    for (var i = 0; i < columns.size(); i++) {
                        columns.get(i).add(values[i]);
                    }
                } else {
                    throw new IllegalStateException("Invalid csv file structure: line " + lineNumber + " has " + values.length + " columns, expecting " + columns.size() + " columns");
                }
            }
        }
    }

    public CsvDataLoader mapColumn(int columnIndex, Map<String, String> valuesMap) {
        columns.set(
                columnIndex,
                columns.get(columnIndex)
                        .stream()
                        .map(valuesMap::get)
                        .toList()
        );
        return this;
    }

    public NumericData toNumericData() {
        return new NumericData(
                columns.stream()
                        .map(column -> column.stream().map(Double::parseDouble).collect(Collectors.toCollection(ArrayList::new)))
                        .collect(Collectors.toCollection(ArrayList::new))
        );
    }

    public static void saveToFile(String file, NumericData numericData) throws IOException {
        var data = numericData.toDoubleArray(0, numericData.getColumnsCount() - 1);
        try (var fileWriter = new BufferedWriter(new FileWriter(file))) {
            for (var row : data) {
                for (var i = 0; i < row.length; i++) {
                    fileWriter.append(String.valueOf(row[i]));
                    if (i < row.length - 1) {
                        fileWriter.append(DELIMITER);
                    } else {
                        fileWriter.append('\n');
                    }
                }
            }
        }
    }
}
