package com.floorestimatepro.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.floorestimatepro.model.Calibration;
import com.floorestimatepro.model.EstimateCalculator;
import com.floorestimatepro.model.EstimateResult;
import com.floorestimatepro.model.PlanPoint;
import com.floorestimatepro.model.Project;
import com.floorestimatepro.persistence.ProjectStore;
import com.floorestimatepro.export.EstimateExporter;

public class MainFrame extends JFrame{
    Project project = new Project();
    PlanCanvas canvas = new PlanCanvas(project);
    JTextField knownFeetField = new JTextField("10", 5);
    JTextField wasteField = new JTextField("1.10", 5);
    JTextField priceField = new JTextField("5.0", 5);
    JLabel statusLabel = new JLabel("Open a plan image to start");
    JLabel resultLabel = new JLabel("Estimate: —");

    public MainFrame(){
        setTitle("Floor Estimate Pro");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel toolbar2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton openBtn = new JButton("Open Image");
        JButton calibrateBtn = new JButton("Calibrate");
        JButton applyCalBtn = new JButton("Apply Calibration");
        JButton roomBtn = new JButton("Draw Room");
        JButton finishRoomBtn = new JButton("Finish Room");
        JButton obstacleBtn = new JButton("Draw Obstacle");
        JButton finishObstacleBtn = new JButton("Finish Obstacle");
        JButton estimateBtn = new JButton("Estimate");
        JButton finishEstimateBtn = new JButton("Finish Estimate");
        JButton saveBtn = new JButton("Save");
        JButton loadBtn = new JButton("Load");
        JButton exportBtn = new JButton("Export");
        JButton zoomInBtn = new JButton("Zoom In");
        JButton zoomOutBtn = new JButton("Zoom Out");
        JButton zoomResetBtn = new JButton("Zoom 100%");

        toolbar.add(openBtn);
        toolbar.add(zoomInBtn);
        toolbar.add(zoomOutBtn);
        toolbar.add(zoomResetBtn);
        toolbar.add(calibrateBtn);
        toolbar.add(new JLabel("Feet:"));
        toolbar.add(knownFeetField);
        toolbar.add(applyCalBtn);
        toolbar.add(roomBtn);
        toolbar.add(finishRoomBtn);
        toolbar.add(obstacleBtn);
        toolbar.add(finishObstacleBtn);

        toolbar2.add(new JLabel("Waste:"));
        toolbar2.add(wasteField);
        toolbar2.add(new JLabel("Price:"));
        toolbar2.add(priceField);
        toolbar2.add(estimateBtn);
        toolbar2.add(finishEstimateBtn);
        toolbar2.add(saveBtn);
        toolbar2.add(loadBtn);
        toolbar2.add(exportBtn);

        JPanel north = new JPanel(new BorderLayout());
        north.add(toolbar, BorderLayout.NORTH);
        north.add(toolbar2, BorderLayout.SOUTH);

        JPanel south = new JPanel(new BorderLayout());
        south.add(statusLabel, BorderLayout.WEST);
        south.add(resultLabel, BorderLayout.EAST);

        add(north, BorderLayout.NORTH);
        add(new JScrollPane(canvas), BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);

        canvas.setOnChanged(this::refreshStatus);

        openBtn.addActionListener(e -> onOpenImage());
        zoomInBtn.addActionListener(e -> canvas.zoomIn());
        zoomOutBtn.addActionListener(e -> canvas.zoomOut());
        zoomResetBtn.addActionListener(e -> canvas.resetZoom());
        calibrateBtn.addActionListener(e -> onCalibrateTool());
        applyCalBtn.addActionListener(e -> onApplyCalibration());
        roomBtn.addActionListener(e -> onDrawRoomTool());
        finishRoomBtn.addActionListener(e -> onFinishRoom());
        obstacleBtn.addActionListener(e -> onDrawObstacleTool());
        finishObstacleBtn.addActionListener(e -> onFinishObstacle());
        estimateBtn.addActionListener(e -> refreshEstimate());
        finishEstimateBtn.addActionListener(e -> onFinishEstimate());
        saveBtn.addActionListener(e -> onSave());
        loadBtn.addActionListener(e -> onLoad());
        exportBtn.addActionListener(e -> onExport());

        pack();
        setLocationRelativeTo(null);
    }

    void onOpenImage(){
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Images", "png", "jpg", "jpeg", "gif"));
        if(chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION){
            return;
        }
        File file = chooser.getSelectedFile();
        project.setImagePath(file.getAbsolutePath());
        canvas.loadImageFromProject();
        statusLabel.setText("Loaded " + file.getName());
    }

    void onCalibrateTool(){
        canvas.setToolMode(ToolMode.CALIBRATE);
        statusLabel.setText("Click two ends of a known length, enter feet, then Apply Calibration");
    }

    void onApplyCalibration(){
        if(canvas.pendingPts().size() < 2){
            JOptionPane.showMessageDialog(this, "Click two calibration points first");
            return;
        }
        try{
            double knownFeet = Double.parseDouble(knownFeetField.getText().trim());
            PlanPoint a = canvas.pendingPts().get(0);
            PlanPoint b = canvas.pendingPts().get(1);
            Calibration cal = Calibration.fromKnownLength(a, b, knownFeet);
            project.setCalibration(cal);
            canvas.pendingPts().clear();
            canvas.repaint();
            statusLabel.setText("Calibrated: " + cal.feetPerPixel() + " ft/px");
            refreshEstimate();
        }catch(Exception ex){
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    void onDrawRoomTool(){
        canvas.setToolMode(ToolMode.DRAW_ROOM);
        statusLabel.setText("Click room corners, then Finish Room");
    }

    void onFinishRoom(){
        try{
            canvas.finishRoom();
            statusLabel.setText("Room added (" + project.roomCount() + ")");
            refreshEstimate();
        }catch(Exception ex){
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    void onDrawObstacleTool(){
        canvas.setToolMode(ToolMode.DRAW_OBSTACLE);
        statusLabel.setText("Click obstacle corners, then Finish Obstacle");
    }

    void onFinishObstacle(){
        try{
            canvas.finishObstacle();
            statusLabel.setText("Obstacle added (" + project.obstacleCount() + ")");
            refreshEstimate();
        }catch(Exception ex){
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    void refreshStatus(){
        String zoomText = " | Zoom: " + Math.round(canvas.getZoom() * 100) + "%";
        if(canvas.getToolMode() == ToolMode.CALIBRATE){
            statusLabel.setText("Calibration clicks: " + canvas.pendingPts().size() + "/2" + zoomText);
        }else if(canvas.getToolMode() == ToolMode.DRAW_ROOM){
            statusLabel.setText("Room corners: " + canvas.pendingPts().size() + zoomText);
        }else if(canvas.getToolMode() == ToolMode.DRAW_OBSTACLE){
            statusLabel.setText("Obstacle corners: " + canvas.pendingPts().size() + zoomText);
        }else{
            statusLabel.setText("Zoom: " + Math.round(canvas.getZoom() * 100) + "%");
        }
    }

    void refreshEstimate(){
        if(!project.isCalibrated()){
            resultLabel.setText("Estimate: calibrate first");
            return;
        }
        if(project.rooms().isEmpty()){
            resultLabel.setText("Estimate: add a room first");
            return;
        }
        try{
            double waste = Double.parseDouble(wasteField.getText().trim());
            double price = Double.parseDouble(priceField.getText().trim());
            EstimateResult result = EstimateCalculator.calculate(
                project.rooms(),
                project.obstacles(),
                project.calibration(),
                waste,
                price
            );
            resultLabel.setText(
                "Net px2: " + round(result.netPixelArea())
                + " | Sq ft: " + round(result.netRealArea())
                + " | Material: " + round(result.materialSqFt())
                + " | Cost: $" + round(result.estimatedCost())
            );
        }catch(Exception ex){
            resultLabel.setText("Estimate error: " + ex.getMessage());
        }
    }

    void onSave(){
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("JSON", "json"));
        if(chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION){
            return;
        }
        try{
            String path = chooser.getSelectedFile().getAbsolutePath();
            if(!path.endsWith(".json")){
                path = path + ".json";
            }
            ProjectStore.save(project, path);
            statusLabel.setText("Saved " + path);
        }catch(Exception ex){
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    void onLoad(){
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("JSON", "json"));
        if(chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION){
            return;
        }
        try{
            project = ProjectStore.load(chooser.getSelectedFile().getAbsolutePath());
            canvas.setProject(project);
            statusLabel.setText("Loaded project");
            refreshEstimate();
        }catch(Exception ex){
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    void onExport(){
        if(!project.isCalibrated() || project.rooms().isEmpty()){
            JOptionPane.showMessageDialog(this, "Calibrate and add a room before exporting");
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("CSV", "csv"));
        if(chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION){
            return;
        }
        try{
            double waste = Double.parseDouble(wasteField.getText().trim());
            double price = Double.parseDouble(priceField.getText().trim());
            EstimateResult result = EstimateCalculator.calculate(
                project.rooms(),
                project.obstacles(),
                project.calibration(),
                waste,
                price
            );
            String path = chooser.getSelectedFile().getAbsolutePath();
            if(!path.endsWith(".csv")){
                path = path + ".csv";
            }
            EstimateExporter.exportCsv(result, path);
            statusLabel.setText("Exported " + path);
        }catch(Exception ex){
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    void onFinishEstimate(){
        if(!project.isCalibrated()){
            JOptionPane.showMessageDialog(this, "Calibrate first");
            return;
        }
        if(project.rooms().isEmpty()){
            JOptionPane.showMessageDialog(this, "Add a room first");
            return;
        }
        try{
            double waste = Double.parseDouble(wasteField.getText().trim());
            double price = Double.parseDouble(priceField.getText().trim());
            EstimateResult result = EstimateCalculator.calculate(
                project.rooms(),
                project.obstacles(),
                project.calibration(),
                waste,
                price
            );
            EstimateResultsFrame frame = new EstimateResultsFrame(
                result,
                project.roomCount(),
                project.obstacleCount()
            );
            frame.setVisible(true);
        }catch(Exception ex){
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    double round(double value){
        return Math.round(value * 1000.0) / 1000.0;
    }
}
