package com.github.adrian99.neuralnetworkgui.data;

public record StatisticsRecord(
        long epochs,
        double accuracy,
        double error,
        long timeMillis
) {
}
