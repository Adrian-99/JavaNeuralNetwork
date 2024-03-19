package com.github.adrian99.neuralnetworkgui.data;

public record DataConfiguration(
        boolean crossValidationEnabled,
        int crossValidationGroupsCount
) {}
