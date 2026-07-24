package com.floorestimatepro.model;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RoomTest{
    @Test
    void rectangle_has_pixel_area_50(){

        PlanPoint p0 = new PlanPoint(0.0, 0.0);
        PlanPoint p1 = new PlanPoint(10.0, 0.0);
        PlanPoint p2 = new PlanPoint(10.0, 5.0);
        PlanPoint p3 = new PlanPoint(0.0, 5.0);

        Room room = new Room(List.of(p0,p1,p2,p3));

        double actual = room.pixelArea();

        assertEquals(50.0, room.pixelArea(), 0.0001);
    }

}