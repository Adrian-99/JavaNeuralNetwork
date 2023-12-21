package com.github.adrian99.neuralnetworkgui;

import com.github.adrian99.neuralnetworkgui.window.NetworkCreatorWindow;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(NetworkCreatorWindow::new);
    }
}
