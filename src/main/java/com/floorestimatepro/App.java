package com.floorestimatepro;

import javax.swing.SwingUtilities;

import com.floorestimatepro.ui.MainFrame;

public class App{
    public static void main ( String[] args){
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
