from pathlib import Path

from floor_estimate_pro.model.calibration import Calibration
from floor_estimate_pro.model.obstacle import Obstacle
from floor_estimate_pro.model.plan_point import PlanPoint
from floor_estimate_pro.model.project import Project
from floor_estimate_pro.model.room import Room
from floor_estimate_pro.persistence.project_store import ProjectStore


def test_save_then_load_restores_project(tmp_path: Path):
    cal = Calibration(0.1)
    room = Room([
        PlanPoint(0.0, 0.0),
        PlanPoint(10.0, 0.0),
        PlanPoint(10.0, 5.0),
        PlanPoint(0.0, 5.0),
    ])
    obstacle = Obstacle([
        PlanPoint(0.0, 0.0),
        PlanPoint(4.0, 0.0),
        PlanPoint(4.0, 2.0),
        PlanPoint(0.0, 2.0),
    ])
    original = Project()
    original.set_image_path("plans/kitchen.png")
    original.set_calibration(cal)
    original.add_room(room)
    original.add_obstacle(obstacle)
    file_path = str(tmp_path / "test-project.json")

    ProjectStore.save(original, file_path)
    loaded = ProjectStore.load(file_path)

    assert loaded.image_path() == "plans/kitchen.png"
    assert abs(loaded.calibration().feet_per_pixel - 0.1) < 0.0001
    assert loaded.is_calibrated()
    assert len(loaded.rooms()) == 1
    assert abs(loaded.rooms()[0].pixel_area() - 50.0) < 0.0001
    assert len(loaded.obstacles()) == 1
    assert abs(loaded.obstacles()[0].pixel_area() - 8.0) < 0.0001


def test_save_load_uncalibrated_project(tmp_path: Path):
    p = Project()
    p.set_image_path("plans/empty.png")
    ProjectStore.save(p, str(tmp_path / "uncalibrated.json"))
    loaded = ProjectStore.load(str(tmp_path / "uncalibrated.json"))
    assert loaded.image_path() == "plans/empty.png"
    assert loaded.is_calibrated() is False
    assert loaded.room_count() == 0
