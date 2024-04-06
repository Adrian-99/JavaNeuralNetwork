package com.github.adrian99.neuralnetwork.learning.supervisor;

public interface LearningStatisticsProvider {
    double getCurrentAccuracy();
    double getCurrentError();
    long getLearningEpochsCompletedCount();
    long getTotalLearningTimeMillis();
}
