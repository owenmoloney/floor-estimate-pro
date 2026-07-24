package com.floorestimatepro.model;

public record Calibration(double feetPerPixel){

    public static Calibration fromKnownLength( PlanPoint a, PlanPoint b, double knownFeet){

        double pixelDistance = a.distanceTo(b);
        if(pixelDistance == 0){
            throw new IllegalArgumentException("calibration points must not be the same");
        }

        if(knownFeet <= 0){
            throw new IllegalArgumentException("known length must be positive");
        }

        double scale  = knownFeet / pixelDistance;

        return new Calibration(scale);
    }

    public double toFeet(double pixelLength){
        return pixelLength * feetPerPixel;
    }

    public double toSquareFeet(double pixelArea){
        double s = this.feetPerPixel;
        return pixelArea * s * s;
    }


}