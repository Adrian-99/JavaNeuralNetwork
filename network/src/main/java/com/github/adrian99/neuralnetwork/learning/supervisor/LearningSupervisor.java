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
import java.util.stream.IntStream;

import static com.github.adrian99.neuralnetwork.util.Utils.toShuffledList;

public class LearningSupervisor implements LearningStatisticsProvider {
    private final NeuralNetwork neuralNetwork;
    private DataProvider dataProvider;
    private long learningEpochsCompletedCount = 0;
    private long totalLearningTimeMillis = 0;
    private long lastStartSystemMillis = 0;
    private CompletableFuture<LearningStatisticsProvider> asyncLearningCompletableFuture = null;

    public LearningSupervisor(NeuralNetwork neuralNetwork, DataProvider dataProvider) {
        this.neuralNetwork = neuralNetwork;
        this.dataProvider = dataProvider;
    }

    public void setDataProvider(DataProvider dataProvider) {
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
    public long getTotalLearningTimeMillis() {
        return totalLearningTimeMillis +
                (lastStartSystemMillis != 0 ? (System.currentTimeMillis() - lastStartSystemMillis) : 0);
    }

    public void startLearning(Configuration configuration) {
        if (lastStartSystemMillis == 0) {
            lastStartSystemMillis = System.currentTimeMillis();
            learning(configuration);
            totalLearningTimeMillis += System.currentTimeMillis() - lastStartSystemMillis;
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

    private void learning(Configuration configuration) {
        var nextUpdateAtEpochs = learningEpochsCompletedCount + configuration.epochBatchSize;
        while (noEndConditionFulfilled(configuration.endConditions) && isAsyncLearningNotCancelled()) {
            while (learningEpochsCompletedCount < nextUpdateAtEpochs && isAsyncLearningNotCancelled()) {
                learning(configuration.errorFunction, configuration.learningFunction);
            }
            if (configuration.updateCallback != null) {
                CompletableFuture.runAsync(() -> configuration.updateCallback.accept(this));
            }
            while (nextUpdateAtEpochs <= learningEpochsCompletedCount) {
                nextUpdateAtEpochs += configuration.epochBatchSize;
            }
        }
    }

    private void learning(ErrorFunction errorFunction, LearningFunction learningFunction) {
        var learningData = dataProvider.getLearningData();
        if (learningData.getInputs().length == learningData.getTargets().length) {
            IntStream.range(0, learningData.getInputs().length)
                    .boxed()
                    .collect(toShuffledList())
                    .forEach(i -> {
                        neuralNetwork.learnSingleEpoch(
                                learningData.getInputs()[i],
                                learningData.getTargets()[i],
                                errorFunction,
                                learningFunction
                        );
                        learningEpochsCompletedCount++;
                    });
        } else {
            throw new IllegalStateException("Input sets and target output sets counts mismatch: " + learningData.getInputs().length + " != " + learningData.getTargets().length);
        }
        dataProvider.performValidation(neuralNetwork, errorFunction);
    }

    public static class Configuration {
        private final ErrorFunction errorFunction;
        private final LearningFunction learningFunction;
        private final List<EndCondition> endConditions = new ArrayList<>();
        private int epochBatchSize = 1000;
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
