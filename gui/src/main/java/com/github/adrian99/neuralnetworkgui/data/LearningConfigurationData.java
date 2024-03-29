package com.github.adrian99.neuralnetworkgui.data;

import java.util.Optional;

public record LearningConfigurationData(
        int epochBatchSize,
        double learningRate,
        Optional<Integer> crossValidationGroupsCount,
        Optional<Double> accuracyEndConditionValue,
        Optional<Integer> epochsCountEndConditionValue,
        Optional<Double> errorEndConditionValue,
        Optional<Integer> timeEndConditionValue
) {
    public LearningConfigurationData() {
        this(
                1000,
                1,
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        );
    }
}
