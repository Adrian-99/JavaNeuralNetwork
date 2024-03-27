package com.github.adrian99.neuralnetwork.learning.supervisor;

public interface LearningStatisticsProvider {
    boolean isLearningInProgress();
    double getCurrentAccuracy();
    double getCurrentError();
    long getLearningEpochsCompletedCount();
    long getTotalLearningTimeSeconds();
}
