package com.floorestimatepro.model;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class EstimateCalculatorTest{
    @Test
    void room_minus_one_obstacle_then_scale_and_waste(){
        Room room  = new Room (List.of(
            new PlanPoint(0,0), new PlanPoint(10,0),
            new PlanPoint(10,5), new PlanPoint(0,5)
        ));

        Obstacle obstacle = new Obstacle( List.of(
            new PlanPoint(0,0), new PlanPoint(4,0),
            new PlanPoint(4,2), new PlanPoint(0,2)
        ));

        Calibration cal = new Calibration(0.1);

        double wasteFactor = 1.10;
        double pricePerSqFt = 5.0;

        EstimateResult result = EstimateCalculator.calculate(room, List.of(obstacle), cal, wasteFactor, pricePerSqFt);        assertEquals(42.0, result.netPixelArea(), 0.0001);
        assertEquals(0.42, result.netRealArea(), 0.0001);
        assertEquals(0.462, result.materialSqFt(), 0.0001);
        assertEquals(2.31, result.estimatedCost(), 0.0001);       

    }

}