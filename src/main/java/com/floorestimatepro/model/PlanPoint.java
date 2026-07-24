package com.floorestimatepro.model;


//A record is a special class: it stores fields and gives you accessors named:
//x()  and y()  automatically (not getX needed)

public record PlanPoint(double x, double y){
    public double distanceTo(PlanPoint other){
        double dx = this.x() - other.x();
        double dy = this.y() - other.y();
        return Math.sqrt(dx * dx + dy *dy);
    }
}