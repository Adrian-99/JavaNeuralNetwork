package com.github.adrian99.neuralnetwork.learning.endcondition;

import com.github.adrian99.neuralnetwork.learning.supervisor.LearningStatisticsProvider;

public class AccuracyEndCondition implements EndCondition {
    private final double desiredAccuracy;

    public AccuracyEndCondition(double desiredAccuracy) {
        this.desiredAccuracy = desiredAccuracy;
    }

    @Override
    public boolean isFulfilled(LearningStatisticsProvider learningStatisticsProvider) {
        return learningStatisticsProvider.getCurrentAccuracy() >= desiredAccuracy;
    }
}
