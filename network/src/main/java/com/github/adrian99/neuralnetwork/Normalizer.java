package com.github.adrian99.neuralnetwork;

import com.github.adrian99.neuralnetwork.data.csv.CsvDataLoader;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

public class Normalizer {
    public static void main(String[] args) throws URISyntaxException, IOException {
        var numericData = new CsvDataLoader(new File(Main.class.getClassLoader().getResource("datasets/iris/iris.data").toURI()))
                .mapColumn(4, Map.of("Iris-setosa", "1", "Iris-versicolor", "2", "Iris-virginica", "3"))
                .toNumericData()
                .normalize(0)
                .normalize(1)
                .normalize(2)
                .normalize(3);
        CsvDataLoader.saveToFile("normalized.data", numericData);
    }
}
