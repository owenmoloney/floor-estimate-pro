from floor_estimate_pro.model.calibration import Calibration
from floor_estimate_pro.model.obstacle import Obstacle
from floor_estimate_pro.model.plan_point import PlanPoint
from floor_estimate_pro.model.project import Project
from floor_estimate_pro.model.room import Room


def test_project_holds_image_calibration_room_and_obstacle():
    cal = Calibration(0.1)
    room = Room([
        PlanPoint(0.0, 0.0),
        PlanPoint(10.0, 0.0),
        PlanPoint(10.0, 5.0),
        PlanPoint(0.0, 5.0),
    ])
    assert abs(room.pixel_area() - 50.0) < 0.0001

    obstacle = Obstacle([
        PlanPoint(0.0, 0.0),
        PlanPoint(4.0, 0.0),
        PlanPoint(4.0, 2.0),
        PlanPoint(0.0, 2.0),
    ])
    assert abs(obstacle.pixel_area() - 8.0) < 0.0001

    project = Project()
    project.set_image_path("plans/kitchen.png")
    project.set_calibration(cal)
    project.add_room(room)
    project.add_obstacle(obstacle)

    assert project.image_path() == "plans/kitchen.png"
    assert abs(project.calibration().feet_per_pixel - 0.1) < 0.0001
    assert project.is_calibrated()
    assert len(project.rooms()) == 1
    assert abs(project.rooms()[0].pixel_area() - 50.0) < 0.0001
    assert len(project.obstacles()) == 1
    assert abs(project.obstacles()[0].pixel_area() - 8.0) < 0.0001
