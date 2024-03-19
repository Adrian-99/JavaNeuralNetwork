package com.github.adrian99.neuralnetwork.learning.endcondition;

import com.github.adrian99.neuralnetwork.learning.supervisor.LearningStatisticsProvider;

public class TimeEndCondition implements EndCondition {
    private final long maxTimeSeconds;

    public TimeEndCondition(long maxTimeSeconds) {
        this.maxTimeSeconds = maxTimeSeconds;
    }

    @Override
    public boolean isFulfilled(LearningStatisticsProvider learningStatisticsProvider) {
        return learningStatisticsProvider.getTotalLearningTimeSeconds() >= maxTimeSeconds;
    }
}
