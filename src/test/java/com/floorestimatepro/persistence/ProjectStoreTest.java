package com.floorestimatepro.persistence;

import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.floorestimatepro.model.Calibration;
import com.floorestimatepro.model.Obstacle;
import com.floorestimatepro.model.PlanPoint;
import com.floorestimatepro.model.Project;
import com.floorestimatepro.model.Room;

public class ProjectStoreTest {
    @Test
    void save_then_load_restores_project() throws Exception {
        Calibration cal = new Calibration(0.1);

        Room room = new Room(List.of(
            new PlanPoint(0.0, 0.0),
            new PlanPoint(10.0, 0.0),
            new PlanPoint(10.0, 5.0),
            new PlanPoint(0.0, 5.0)
        ));

        Obstacle obstacle = new Obstacle(List.of(
            new PlanPoint(0.0, 0.0),
            new PlanPoint(4.0, 0.0),
            new PlanPoint(4.0, 2.0),
            new PlanPoint(0.0, 2.0)
        ));

        Project original = new Project();
        original.setImagePath("plans/kitchen.png");
        original.setCalibration(cal);
        original.addRoom(room);
        original.addObstacle(obstacle);
        String filePath = "target/test-project.json";

        ProjectStore.save(original, filePath);

        Project loaded = ProjectStore.load(filePath);

        assertEquals("plans/kitchen.png", loaded.imagePath());
        assertEquals(0.1, loaded.calibration().feetPerPixel(), 0.0001);
        assertTrue(loaded.isCalibrated());
        assertEquals(1, loaded.rooms().size());
        assertEquals(50.0, loaded.rooms().get(0).pixelArea(), 0.0001);
        assertEquals(1, loaded.obstacles().size());
        assertEquals(8.0, loaded.obstacles().get(0).pixelArea(), 0.0001);
    }

    @Test
    void save_load_uncalibrated_project() throws Exception {
        Project p = new Project();
        p.setImagePath("plans/empty.png");
        ProjectStore.save(p, "target/uncalibrated.json");
        Project loaded = ProjectStore.load("target/uncalibrated.json");
        assertEquals("plans/empty.png", loaded.imagePath());
        assertTrue(loaded.isCalibrated() == false);
        assertEquals(0, loaded.roomCount());
    }
}
