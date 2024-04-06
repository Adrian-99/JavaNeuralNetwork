package com.github.adrian99.neuralnetworkgui.util;

import com.github.adrian99.neuralnetwork.learning.supervisor.LearningStatisticsProvider;
import com.github.adrian99.neuralnetworkgui.data.StatisticsRecord;

import java.util.LinkedList;
import java.util.List;

public class StatisticsCollector {
    private final List<StatisticsRecord> statistics;

    public StatisticsCollector() {
        statistics = new LinkedList<>();
    }

    public void collect(LearningStatisticsProvider statisticsProvider) {
        statistics.add(
                new StatisticsRecord(
                        statisticsProvider.getLearningEpochsCompletedCount(),
                        statisticsProvider.getCurrentAccuracy(),
                        statisticsProvider.getCurrentError(),
                        statisticsProvider.getTotalLearningTimeMillis()
                )
        );
    }

    public List<StatisticsRecord> getStatistics() {
        return statistics;
    }
}
