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
    private final List<Double> accuracyBuffer;
    private final List<Double> errorBuffer;
    private int currentValidationGroupIndex;
    private InputsAndTargets currentValidationData;

    public CrossValidationDataProvider(double[][] inputs, int[][] targets, int groupsCount) {
        super(inputs, targets);
        if (groupsCount > 1) {
            if (groupsCount <= inputs.length) {
                this.groupsCount = groupsCount;
                groupsIndexes = new ArrayList<>();
                accuracyBuffer = new ArrayList<>();
                errorBuffer = new ArrayList<>();
                calculateNewGroups();
            } else {
                throw new IllegalArgumentException("Groups count must not be greater than inputs length");
            }
        } else {
            throw new IllegalArgumentException("Groups count must be greater than 1");
        }
    }

    @Override
    public InputsAndTargets getLearningData() {
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

        return new InputsAndTargets(learningInputs.toArray(double[][]::new), learningTargets.toArray(int[][]::new));
    }

    @Override
    public void performValidation(NeuralNetwork neuralNetwork, ErrorFunction errorFunction) {
        var networkOutputs = neuralNetwork.activate(currentValidationData.getInputs());
        accuracyBuffer.add(Statistics.accuracy(networkOutputs, currentValidationData.getTargets()));
        errorBuffer.add(Statistics.error(networkOutputs, currentValidationData.getTargets(), errorFunction));

        currentValidationGroupIndex++;
        if (currentValidationGroupIndex >= groupsCount) {
            accuracy = accuracyBuffer.stream().mapToDouble(Double::doubleValue).average().orElse(0);
            accuracyBuffer.clear();
            error = errorBuffer.stream().mapToDouble(Double::doubleValue).sum();
            errorBuffer.clear();
            calculateNewGroups();
        }
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
