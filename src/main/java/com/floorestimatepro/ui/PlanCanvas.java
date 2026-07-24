package com.floorestimatepro.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

import com.floorestimatepro.model.Obstacle;
import com.floorestimatepro.model.PlanPoint;
import com.floorestimatepro.model.Project;
import com.floorestimatepro.model.Room;

public class PlanCanvas extends JPanel{
    Project project;
    ToolMode toolMode = ToolMode.NONE;
    BufferedImage planImage;
    List<PlanPoint> pendingPts = new ArrayList<>();
    Runnable onChanged;
    double zoom = 1.0;

    public PlanCanvas(Project project){
        this.project = project;
        setPreferredSize(new Dimension(900, 600));
        setBackground(Color.DARK_GRAY);

        addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                onMouseClick(e.getX(), e.getY());
            }
        });

        addMouseWheelListener(new MouseWheelListener(){
            @Override
            public void mouseWheelMoved(MouseWheelEvent e){
                if(e.getPreciseWheelRotation() < 0){
                    zoomIn();
                }else if(e.getPreciseWheelRotation() > 0){
                    zoomOut();
                }
            }
        });
    }

    public void setProject(Project project){
        this.project = project;
        pendingPts.clear();
        loadImageFromProject();
        repaint();
    }

    public Project getProject(){
        return project;
    }

    public void setToolMode(ToolMode mode){
        this.toolMode = mode;
        pendingPts.clear();
        repaint();
    }

    public ToolMode getToolMode(){
        return toolMode;
    }

    public void setOnChanged(Runnable onChanged){
        this.onChanged = onChanged;
    }

    public double getZoom(){
        return zoom;
    }

    public void zoomIn(){
        setZoom(zoom * 1.25);
    }

    public void zoomOut(){
        setZoom(zoom / 1.25);
    }

    public void resetZoom(){
        setZoom(1.0);
    }

    public void setZoom(double newZoom){
        if(newZoom < 0.25){
            newZoom = 0.25;
        }
        if(newZoom > 8.0){
            newZoom = 8.0;
        }
        zoom = newZoom;
        updateCanvasSize();
        repaint();
        notifyChanged();
    }

    public void loadImageFromProject(){
        try{
            if(project.imagePath() == null){
                planImage = null;
                return;
            }
            planImage = ImageIO.read(new File(project.imagePath()));
            updateCanvasSize();
        }catch(Exception e){
            planImage = null;
        }
        repaint();
    }

    void updateCanvasSize(){
        if(planImage != null){
            int w = (int)(planImage.getWidth() * zoom);
            int h = (int)(planImage.getHeight() * zoom);
            setPreferredSize(new Dimension(w, h));
            revalidate();
        }else{
            setPreferredSize(new Dimension((int)(900 * zoom), (int)(600 * zoom)));
            revalidate();
        }
    }

    public void finishRoom(){
        if(pendingPts.size() < 3){
            throw new IllegalArgumentException("Need at least 3 corners");
        }
        project.addRoom(new Room(new ArrayList<>(pendingPts)));
        pendingPts.clear();
        repaint();
        notifyChanged();
    }

    public void finishObstacle(){
        if(pendingPts.size() < 3){
            throw new IllegalArgumentException("Need at least 3 corners");
        }
        project.addObstacle(new Obstacle(new ArrayList<>(pendingPts)));
        pendingPts.clear();
        repaint();
        notifyChanged();
    }

    public List<PlanPoint> pendingPts(){
        return pendingPts;
    }

    void onMouseClick(int screenX, int screenY){
        double imageX = screenX / zoom;
        double imageY = screenY / zoom;
        PlanPoint point = new PlanPoint(imageX, imageY);

        if(toolMode == ToolMode.CALIBRATE){
            pendingPts.add(point);
        }else if(toolMode == ToolMode.DRAW_ROOM){
            pendingPts.add(point);
        }else if(toolMode == ToolMode.DRAW_OBSTACLE){
            pendingPts.add(point);
        }

        repaint();
        notifyChanged();
    }

    void notifyChanged(){
        if(onChanged != null){
            onChanged.run();
        }
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.scale(zoom, zoom);

        if(planImage != null){
            g2.drawImage(planImage, 0, 0, null);
        }

        g2.setStroke(new BasicStroke((float)(2.0 / zoom)));

        g2.setColor(new Color(30, 144, 255, 180));
        for(Room room : project.rooms()){
            drawPolygon(g2, room.listOfPoints(), true);
        }

        g2.setColor(new Color(220, 20, 60, 180));
        for(Obstacle obstacle : project.obstacles()){
            drawPolygon(g2, obstacle.listOfPoints(), true);
        }

        g2.setColor(Color.YELLOW);
        drawPolygon(g2, pendingPts, false);
        int dot = Math.max(2, (int)(4 / zoom));
        for(PlanPoint p : pendingPts){
            g2.fillOval((int)p.x() - dot, (int)p.y() - dot, dot * 2, dot * 2);
        }
    }

    void drawPolygon(Graphics2D g2, List<PlanPoint> points, boolean closed){
        if(points == null || points.size() < 2){
            return;
        }
        for(int i = 0; i < points.size() - 1; i++){
            PlanPoint a = points.get(i);
            PlanPoint b = points.get(i + 1);
            g2.drawLine((int)a.x(), (int)a.y(), (int)b.x(), (int)b.y());
        }
        if(closed && points.size() >= 3){
            PlanPoint first = points.get(0);
            PlanPoint last = points.get(points.size() - 1);
            g2.drawLine((int)last.x(), (int)last.y(), (int)first.x(), (int)first.y());
        }
    }
}
