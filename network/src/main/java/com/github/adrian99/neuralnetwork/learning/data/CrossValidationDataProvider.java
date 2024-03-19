package com.github.adrian99.neuralnetwork.learning.data;

import com.github.adrian99.neuralnetwork.NeuralNetwork;
import com.github.adrian99.neuralnetwork.learning.error.ErrorFunction;
import com.github.adrian99.neuralnetwork.util.Statistics;

import java.util.*;
import java.util.stream.IntStream;

import static com.github.adrian99.neuralnetwork.util.Utils.toShuffledList;

public class CrossValidationDataProvider extends DataProvider {
    private final int groupsCount;
    private final List<Set<Integer>> groupsIndexes;
    private final List<Double> accuracyHistory;
    private final List<Double> errorHistory;
    private int currentValidationGroupIndex;
    private InputsAndTargets currentValidationData;

    public CrossValidationDataProvider(double[][] inputs, int[][] targets, int groupsCount) {
        super(inputs, targets);
        if (groupsCount > 0) {
            if (groupsCount <= inputs.length) {
                this.groupsCount = groupsCount;
                groupsIndexes = new ArrayList<>();
                accuracyHistory = new ArrayList<>();
                errorHistory = new ArrayList<>();
                currentValidationGroupIndex = groupsCount;
            } else {
                throw new IllegalArgumentException("Groups count must not be greater than inputs length");
            }
        } else {
            throw new IllegalArgumentException("Groups count must be greater than 0");
        }
    }

    @Override
    public LearningAndValidationData getData() {
        currentValidationGroupIndex++;
        if (currentValidationGroupIndex >= groupsCount) {
            calculateNewGroups();
        }

        var learningInputs = new ArrayList<double[]>();
        var learningTargets = new ArrayList<int[]>();
        var validationInputs = new ArrayList<double[]>();
        var validationTargets = new ArrayList<int[]>();

        groupsIndexes.get(currentValidationGroupIndex).forEach(index -> {
            validationInputs.add(inputs[index]);
            validationTargets.add(targets[index]);
        });
        IntStream.range(0, groupsCount)
                .filter(groupIndex -> groupIndex != currentValidationGroupIndex)
                .mapToObj(groupsIndexes::get)
                .flatMap(Collection::stream)
                .forEach(index -> {
                    learningInputs.add(inputs[index]);
                    learningTargets.add(targets[index]);
                });

        currentValidationData = new InputsAndTargets(validationInputs.toArray(double[][]::new), validationTargets.toArray(int[][]::new));

        return new LearningAndValidationData(
                new InputsAndTargets(learningInputs.toArray(double[][]::new), learningTargets.toArray(int[][]::new)),
                currentValidationData
        );
    }

    @Override
    public void calculateAccuracyAndError(NeuralNetwork neuralNetwork, ErrorFunction errorFunction) {
        var networkOutputs = neuralNetwork.activate(currentValidationData.getInputs());

        var currentAccuracy = Statistics.accuracy(networkOutputs, currentValidationData.getTargets());
        if (accuracyHistory.size() >= groupsCount) {
            accuracyHistory.remove(0);
        }
        accuracyHistory.add(currentAccuracy);

        var currentError = Statistics.error(networkOutputs, currentValidationData.getTargets(), errorFunction);
        if (errorHistory.size() >= groupsCount) {
            errorHistory.remove(0);
        }
        errorHistory.add(currentError);
    }

    @Override
    public double getAccuracy() {
        return accuracyHistory.stream().mapToDouble(Double::doubleValue).average().orElse(0);
    }

    @Override
    public double getError() {
        return errorHistory.stream().mapToDouble(Double::doubleValue).average().orElse(Double.MAX_VALUE);
    }

    private void calculateNewGroups() {
        groupsIndexes.clear();
        var groupsSize = inputs.length / groupsCount;
        var largerGroupsCount = inputs.length % groupsCount;
        var currentGroup = new HashSet<Integer>();
        IntStream.range(0, inputs.length)
                .boxed()
                .collect(toShuffledList())
                .forEach(index -> {
                    currentGroup.add(index);
                    if ((groupsIndexes.size() < largerGroupsCount && currentGroup.size() == groupsSize + 1)
                            || currentGroup.size() == groupsSize) {
                        groupsIndexes.add(new HashSet<>(currentGroup));
                        currentGroup.clear();
                    }
                });
        currentValidationGroupIndex = 0;
    }
}
