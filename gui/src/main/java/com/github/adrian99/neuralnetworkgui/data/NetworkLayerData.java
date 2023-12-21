package com.github.adrian99.neuralnetworkgui.data;

import java.util.List;

public record NetworkLayerData(
        int neuronsCount,
        String activationFunctionClass,
        List<Double> activationFunctionParameters,
        String weightInitializationFunctionClass
) {
}
