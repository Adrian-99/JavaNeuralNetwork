package com.github.adrian99.neuralnetwork.learning.supervisor;

import com.github.adrian99.neuralnetwork.NeuralNetwork;
import com.github.adrian99.neuralnetwork.learning.LearningFunction;
import com.github.adrian99.neuralnetwork.learning.data.DataProvider;
import com.github.adrian99.neuralnetwork.learning.endcondition.EndCondition;
import com.github.adrian99.neuralnetwork.learning.error.ErrorFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class LearningSupervisor implements LearningStatisticsProvider {
    private final NeuralNetwork neuralNetwork;
    private final DataProvider dataProvider;
    private long learningEpochsCompletedCount = 0;
    private long totalLearningTimeSeconds = 0;
    private long lastStartSystemMillis = 0;
    private CompletableFuture<LearningStatisticsProvider> asyncLearningCompletableFuture = null;

    public LearningSupervisor(NeuralNetwork neuralNetwork, DataProvider dataProvider) {
        this.neuralNetwork = neuralNetwork;
        this.dataProvider = dataProvider;
    }

    @Override
    public double getCurrentAccuracy() {
        return dataProvider.getAccuracy();
    }

    @Override
    public double getCurrentError() {
        return dataProvider.getError();
    }

    @Override
    public long getLearningEpochsCompletedCount() {
        return learningEpochsCompletedCount;
    }

    @Override
    public long getTotalLearningTimeSeconds() {
        return totalLearningTimeSeconds +
                lastStartSystemMillis != 0 ? (System.currentTimeMillis() - lastStartSystemMillis) / 1000 : 0;
    }

    public void startLearning(Configuration configuration) {
        if (lastStartSystemMillis == 0) {
            lastStartSystemMillis = System.currentTimeMillis();
            while (noEndConditionFulfilled(configuration.endConditions) && isAsyncLearningNotCancelled()) {
                for (var i = 0; i < configuration.epochBatchSize && isAsyncLearningNotCancelled(); i++) {
                    singleEpochLearning(configuration.errorFunction, configuration.learningFunction);
                }
                if (configuration.updateCallback != null) {
                    CompletableFuture.runAsync(() -> configuration.updateCallback.accept(this));
                }
            }
            totalLearningTimeSeconds += (System.currentTimeMillis() - lastStartSystemMillis) / 1000;
            asyncLearningCompletableFuture = null;
            lastStartSystemMillis = 0;
        } else {
            throw new IllegalStateException("Learning is already in progress");
        }
    }

    public CompletableFuture<LearningStatisticsProvider> startLearningAsync(Configuration configuration) {
        if ((asyncLearningCompletableFuture == null || asyncLearningCompletableFuture.isDone()) && lastStartSystemMillis == 0) {
            asyncLearningCompletableFuture = CompletableFuture.runAsync(() -> startLearning(configuration))
                    .thenApply(v -> this);
            return asyncLearningCompletableFuture;
        } else {
            throw new IllegalStateException("Learning is already in progress");
        }
    }

    private boolean noEndConditionFulfilled(List<EndCondition> endConditions) {
        return endConditions.stream().noneMatch(c -> c.isFulfilled(this));
    }

    private boolean isAsyncLearningNotCancelled() {
        return asyncLearningCompletableFuture == null || !asyncLearningCompletableFuture.isCancelled();
    }

    private void singleEpochLearning(ErrorFunction errorFunction, LearningFunction learningFunction) {
        var epochData = dataProvider.getData();
        neuralNetwork.learnSingleEpoch(epochData.learningData().getInputs(), epochData.learningData().getTargets(), errorFunction, learningFunction);
        dataProvider.calculateAccuracyAndError(neuralNetwork, errorFunction);
        learningEpochsCompletedCount++;
    }

    public static class Configuration {
        private final ErrorFunction errorFunction;
        private final LearningFunction learningFunction;
        private final List<EndCondition> endConditions = new ArrayList<>();
        private int epochBatchSize = 10;
        private Consumer<LearningStatisticsProvider> updateCallback = null;

        public Configuration(ErrorFunction errorFunction, LearningFunction learningFunction) {
            this.errorFunction = errorFunction;
            this.learningFunction = learningFunction;
        }

        public Configuration setEpochBatchSize(int epochBatchSize) {
            this.epochBatchSize = epochBatchSize;
            return this;
        }

        public Configuration addEndCondition(EndCondition endCondition) {
            this.endConditions.add(endCondition);
            return this;
        }

        public Configuration setUpdateCallback(Consumer<LearningStatisticsProvider> updateCallback) {
            this.updateCallback = updateCallback;
            return this;
        }
    }
}
