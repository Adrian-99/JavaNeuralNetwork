package com.github.adrian99.neuralnetworkgui.data;

import com.github.adrian99.neuralnetwork.learning.supervisor.LearningSupervisor;

public record LearningConfigurationData(
        int crossValidationGroupsCount,
        LearningSupervisor.Configuration learningConfiguration
) {
}
