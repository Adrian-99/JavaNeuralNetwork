package com.github.adrian99.neuralnetwork.learning.endcondition;

import com.github.adrian99.neuralnetwork.learning.supervisor.LearningStatisticsProvider;

public class TimeEndCondition implements EndCondition {
    private final long maxTimeMillis;

    public TimeEndCondition(long maxTimeSeconds) {
        this.maxTimeMillis = maxTimeSeconds * 1000;
    }

    @Override
    public boolean isFulfilled(LearningStatisticsProvider learningStatisticsProvider) {
        return learningStatisticsProvider.getTotalLearningTimeMillis() >= maxTimeMillis;
    }
}
