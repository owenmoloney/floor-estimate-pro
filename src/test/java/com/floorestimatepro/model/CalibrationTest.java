package com.floorestimatepro.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalibrationTest{
    @Test
    void fromKnownLength_100_pixels_and_10_feet_gives_0_1_ft_per_px(){
        PlanPoint a = new PlanPoint(0.0, 0.0);
        PlanPoint b = new PlanPoint(100.0, 0.0);
        double knownFeet = 10;

        Calibration cal  = Calibration.fromKnownLength(a,b,knownFeet);

        assertEquals(0.1, cal.feetPerPixel(), 0.0001);
    }
}