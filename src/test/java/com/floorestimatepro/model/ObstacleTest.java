package com.floorestimatepro.model;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class ObstacleTest{
    @Test
    void rectangle_has_pixel_area_8(){
        PlanPoint p0 = new PlanPoint(0.0, 0.0);
        PlanPoint p1 = new PlanPoint(4.0, 0.0);
        PlanPoint p2 = new PlanPoint(4.0, 2.0);
        PlanPoint p3 = new PlanPoint(0.0, 2.0);

        Obstacle obstacle  = new Obstacle(List.of(p0,p1,p2,p3));

        double actual = obstacle.pixelArea();

        assertEquals(8.0, obstacle.pixelArea(), 0.0001);
    } 
}