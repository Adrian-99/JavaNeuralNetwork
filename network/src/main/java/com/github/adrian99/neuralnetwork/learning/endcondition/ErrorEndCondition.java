package com.github.adrian99.neuralnetwork.learning.endcondition;

import com.github.adrian99.neuralnetwork.learning.supervisor.LearningStatisticsProvider;

public class ErrorEndCondition implements EndCondition {
    private final double desiredError;

    public ErrorEndCondition(double desiredError) {
        this.desiredError = desiredError;
    }

    @Override
    public boolean isFulfilled(LearningStatisticsProvider learningStatisticsProvider) {
        return learningStatisticsProvider.getCurrentError() <= desiredError;
    }
}
