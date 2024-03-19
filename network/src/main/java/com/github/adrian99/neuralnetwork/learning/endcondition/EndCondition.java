package com.github.adrian99.neuralnetwork.learning.endcondition;

import com.github.adrian99.neuralnetwork.learning.supervisor.LearningStatisticsProvider;

@FunctionalInterface
public interface EndCondition {
    boolean isFulfilled(LearningStatisticsProvider learningStatisticsProvider);
}
