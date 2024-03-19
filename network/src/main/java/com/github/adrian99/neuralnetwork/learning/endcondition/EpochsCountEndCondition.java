package com.github.adrian99.neuralnetwork.learning.endcondition;

import com.github.adrian99.neuralnetwork.learning.supervisor.LearningStatisticsProvider;

public class EpochsCountEndCondition implements EndCondition {
    private final long maxEpochsCount;

    public EpochsCountEndCondition(long maxEpochsCount) {
        this.maxEpochsCount = maxEpochsCount;
    }

    @Override
    public boolean isFulfilled(LearningStatisticsProvider learningStatisticsProvider) {
        return learningStatisticsProvider.getLearningEpochsCompletedCount() >= maxEpochsCount;
    }
}
