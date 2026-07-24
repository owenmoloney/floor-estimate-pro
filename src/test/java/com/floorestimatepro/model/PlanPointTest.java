package com.floorestimatepro.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlanPointTest{
    @Test
    void  distance_of_3_4_5_triangle_is_5() {
        PlanPoint origin = new PlanPoint(0.0, 0.0);
        PlanPoint corner = new PlanPoint(3.0, 4.0);

        double actual  = origin.distanceTo(corner);

        assertEquals(5.0, actual, 0.0001);
    }
}