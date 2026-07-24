package com.floorestimatepro.model;
import java.util.List;


public class EstimateCalculator{
    public static EstimateResult calculate(Room room, List<Obstacle> obstacles, Calibration calibration, double wasteFactor, double pricePerSqFt){
        double roomArea = room.pixelArea();

        double  obstacleTotal = 0;
            for(Obstacle obstacle : obstacles){
                obstacleTotal = obstacleTotal + obstacle.pixelArea();
            }

        double netPixelArea = roomArea - obstacleTotal;

            if(netPixelArea< 0){
                throw new IllegalArgumentException("obstacles larger than room");
            }
        
        double netRealArea = calibration.toSquareFeet(netPixelArea);
        
        double materialSqFt =  netRealArea * wasteFactor;

        double estimatedCost = materialSqFt * pricePerSqFt;

        return new EstimateResult(
            netPixelArea,
            netRealArea,
            materialSqFt,
            estimatedCost
        );
    }
}