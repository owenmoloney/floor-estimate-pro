package com.floorestimatepro.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.floorestimatepro.export.EstimateExporter;
import com.floorestimatepro.model.EstimateResult;

public class EstimateResultsFrame extends JFrame{
    EstimateResult result;

    public EstimateResultsFrame(EstimateResult result, int roomCount, int obstacleCount){
        this.result = result;
        setTitle("Finished Estimate");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel panel = new JPanel(new GridLayout(0, 1, 6, 6));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        JLabel title = new JLabel("Estimate Results");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        panel.add(title);
        panel.add(new JLabel("Rooms: " + roomCount));
        panel.add(new JLabel("Obstacles: " + obstacleCount));
        panel.add(new JLabel("Net pixel area: " + round(result.netPixelArea())));
        panel.add(new JLabel("Net area (sq ft): " + round(result.netRealArea())));
        panel.add(new JLabel("Material (sq ft): " + round(result.materialSqFt())));
        panel.add(new JLabel("Estimated cost: $" + round(result.estimatedCost())));

        JPanel buttons = new JPanel();
        JButton exportBtn = new JButton("Export CSV");
        JButton closeBtn = new JButton("Close");
        buttons.add(exportBtn);
        buttons.add(closeBtn);

        exportBtn.addActionListener(e -> onExport());
        closeBtn.addActionListener(e -> dispose());

        add(panel, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(null);
    }

    void onExport(){
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("CSV", "csv"));
        if(chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION){
            return;
        }
        try{
            String path = chooser.getSelectedFile().getAbsolutePath();
            if(!path.endsWith(".csv")){
                path = path + ".csv";
            }
            EstimateExporter.exportCsv(result, path);
            JOptionPane.showMessageDialog(this, "Exported " + path);
        }catch(Exception ex){
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    double round(double value){
        return Math.round(value * 1000.0) / 1000.0;
    }
}
