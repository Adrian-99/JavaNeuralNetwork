package com.github.adrian99.neuralnetworkgui.data;

import java.util.List;

public record NetworkData(
        int inputsCount,
        List<NetworkLayerData> layersData
) {
}
