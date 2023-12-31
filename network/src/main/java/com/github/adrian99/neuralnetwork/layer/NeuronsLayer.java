package com.github.adrian99.neuralnetwork.layer;

import com.github.adrian99.neuralnetwork.layer.neuron.Neuron;

import java.io.Serializable;

public interface NeuronsLayer extends Serializable {
    Neuron[] getNeurons();
}
