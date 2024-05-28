package com.github.adrian99.neuralnetwork;

import com.github.adrian99.neuralnetwork.data.csv.CsvDataLoader;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

public class Normalizer {
    public static void main(String[] args) throws URISyntaxException, IOException {
//        var numericData = new CsvDataLoader(new File(Main.class.getClassLoader().getResource("datasets/iris/iris.data").toURI()))
//                .mapColumn(4, Map.of("Iris-setosa", "1", "Iris-versicolor", "2", "Iris-virginica", "3"))
//                .toNumericData()
//                .normalize(0)
//                .normalize(1)
//                .normalize(2)
//                .normalize(3);

//        var numericData = new CsvDataLoader(new File(Normalizer.class.getClassLoader().getResource("datasets/car-evaluation/car.data").toURI()))
//                .mapColumn(0, Map.of("vhigh", "4", "high", "3", "med", "2", "low", "1"))
//                .mapColumn(1, Map.of("vhigh", "4", "high", "3", "med", "2", "low", "1"))
//                .mapColumn(2, Map.of("5more", "5"))
//                .mapColumn(3, Map.of("more", "5"))
//                .mapColumn(4, Map.of("small", "1", "med", "2", "big", "3"))
//                .mapColumn(5, Map.of("low", "1", "med", "2", "high", "3"))
//                .mapColumn(6, Map.of("unacc", "0", "acc", "1", "good", "2", "vgood", "3"))
//                .toNumericData()
//                .normalize(0)
//                .normalize(1)
//                .normalize(2)
//                .normalize(3)
//                .normalize(4)
//                .normalize(5);

//        var numericData = new CsvDataLoader(new File(Demo.class.getClassLoader().getResource("datasets/wine-quality/winequality-white-no-header.csv").toURI()))
//                .toNumericData()
//                .normalize(0)
//                .normalize(1)
//                .normalize(2)
//                .normalize(3)
//                .normalize(4)
//                .normalize(5)
//                .normalize(6)
//                .normalize(7)
//                .normalize(8)
//                .normalize(9)
//                .normalize(10)
//                .normalize(11, 0, 6);

//        var dataFile = new File(Demo.class.getClassLoader().getResource("datasets/occupancy-detection/datatraining_noheader.txt").toURI());
//        var numericData = new CsvDataLoader(dataFile)
//                .ignoreColumn(0)
//                .ignoreColumn(1)
//                .toNumericData()
//                .normalize(0)
//                .normalize(1)
//                .normalize(2)
//                .normalize(3)
//                .normalize(4);

        var dataFile = new File(Demo.class.getClassLoader().getResource("datasets/heart-failure-prediction/dataset_csv").toURI());
        var numericData = new CsvDataLoader(dataFile)
                .toNumericData()
                .standardize(0)
                .standardize(1)
                .standardize(2)
                .standardize(3)
                .standardize(4)
                .standardize(4)
                .standardize(5)
                .standardize(6)
                .standardize(7)
                .standardize(8)
                .standardize(9)
                .standardize(10)
                .standardize(11);

        CsvDataLoader.saveToFile("dataset_standardized", numericData);
    }
}
