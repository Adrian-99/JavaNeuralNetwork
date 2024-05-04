package com.github.adrian99.neuralnetworkgui.util;

import javax.swing.*;
import java.awt.*;

public class WindowUtils {
    private WindowUtils() {}

    private static ImageIcon icon = null;

    public static Image getIconImage() {
        if (icon == null) {
            var iconUrl = WindowUtils.class.getClassLoader().getResource("icon.png");
            if (iconUrl != null) {
                icon = new ImageIcon(iconUrl);
            }
        }
        return icon != null ? icon.getImage() : null;
    }
}
