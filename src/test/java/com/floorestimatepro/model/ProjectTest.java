package com.floorestimatepro.model;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertTrue;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProjectTest{
    @Test
    void project_holds_image_calibration_room_and_obstacle(){
        Calibration cal = new Calibration(0.1);

        Room room = new Room(List.of(
            new PlanPoint(0.0, 0.0),
            new PlanPoint(10.0, 0.0),
            new PlanPoint(10.0, 5.0),
            new PlanPoint(0.0, 5.0)
        ));

        assertEquals(50.0, room.pixelArea(), 0.0001);
        
        Obstacle obstacle = new Obstacle(List.of(
            new PlanPoint(0.0, 0.0),
            new PlanPoint(4.0, 0.0),
            new PlanPoint(4.0, 2.0),
            new PlanPoint(0.0, 2.0)
        ));
        assertEquals(8.0, obstacle.pixelArea(), 0.0001);


        Project project = new Project();
        project.setImagePath("plans/kitchen.png");
        project.setCalibration(cal);
        project.addRoom(room);
        project.addObstacle(obstacle);

        assertEquals("plans/kitchen.png", project.imagePath());

        assertEquals(0.1, project.calibration().feetPerPixel(), 0.0001);
        assertTrue(project.isCalibrated());

        assertEquals(1, project.rooms().size());
        assertEquals(50.0, project.rooms().get(0).pixelArea(), 0.0001);

        assertEquals(1, project.obstacles().size());

        assertEquals(8.0, project.obstacles().get(0).pixelArea(), 0.0001);


    }
}