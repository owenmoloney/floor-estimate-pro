package com.floorestimatepro.model;
import java.util.List;

public record Obstacle(List<PlanPoint> listOfPoints){
    public double pixelArea(){
        int n = listOfPoints.size();

        if(n<3){
            throw new IllegalArgumentException("An Obstacle needs at least 3 points");
        }
        double sum1 = 0;
        double sum2 = 0;

        for(int i= 0; i<n; i++){
            PlanPoint current = listOfPoints.get(i);
            PlanPoint next = listOfPoints.get((i + 1) % n);

            sum1 = sum1 + (current.x() * next.y());
            sum2 = sum2 + (current.y() * next.x());
        }

        double area  = Math.abs(sum1 - sum2)/2;
        return area;
   }

   public double areaInSquareFeet(Calibration cal){
    double pixelArea = this.pixelArea();
    return cal.toSquareFeet(pixelArea);
   }
}