package com.floorestimatepro.model;
import java.util.List;
import java.util.ArrayList;

public class Project{
    
   
    String imagePath;
    Calibration calibration;
    List<Room> rooms = new ArrayList<>();
    List<Obstacle> obstacles = new ArrayList<>();

   
    public String imagePath(){
        return this.imagePath;
    }
    public Calibration calibration(){
        return this.calibration;
    }
    public List<Room> rooms(){
        return this.rooms;
    }

    public List<Obstacle> obstacles(){
        return this.obstacles;
    }


    public void setCalibration(Calibration cal){
        this.calibration = cal;
    }
    public void addRoom(Room room){
        this.rooms.add(room);
    }

    public void addObstacle( Obstacle obstacle){
        this.obstacles.add(obstacle);
    }

    public void setImagePath(String path){
        this.imagePath = path;
    }

    public boolean isCalibrated(){
        if(this.calibration != null){
            return true;
        }
        else{
            return false;
        }
    }

    public int roomCount(){
        return this.rooms.size();
    }

    public int obstacleCount(){
        return this.obstacles.size();
    }

}







