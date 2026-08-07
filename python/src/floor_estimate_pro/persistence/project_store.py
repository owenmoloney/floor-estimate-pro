from __future__ import annotations

import json
from pathlib import Path

from floor_estimate_pro.model.calibration import Calibration
from floor_estimate_pro.model.obstacle import Obstacle
from floor_estimate_pro.model.plan_point import PlanPoint
from floor_estimate_pro.model.project import Project
from floor_estimate_pro.model.room import Room


class ProjectStore:
    @staticmethod
    def to_data(project: Project) -> dict:
        cal_data = None
        if project.calibration() is not None:
            cal_data = {
                "feetPerPixel": project.calibration().feet_per_pixel
            }

        room_datas = []
        for room in project.rooms():
            point_datas = []
            for point in room.list_of_points:
                point_datas.append({"x": point.x, "y": point.y})
            room_datas.append({"listOfPoints": point_datas})

        obstacle_datas = []
        for obstacle in project.obstacles():
            point_datas = []
            for point in obstacle.list_of_points:
                point_datas.append({"x": point.x, "y": point.y})
            obstacle_datas.append({"listOfPoints": point_datas})

        return {
            "imagePath": project.image_path(),
            "calibration": cal_data,
            "rooms": room_datas,
            "obstacles": obstacle_datas,
        }

    @staticmethod
    def from_data(data: dict) -> Project:
        project = Project()
        project.set_image_path(data.get("imagePath"))

        cal = data.get("calibration")
        if cal is not None:
            project.set_calibration(Calibration(cal["feetPerPixel"]))

        for room_data in data.get("rooms") or []:
            points = []
            for pd in room_data.get("listOfPoints") or []:
                points.append(PlanPoint(pd["x"], pd["y"]))
            project.add_room(Room(points))

        for obstacle_data in data.get("obstacles") or []:
            points = []
            for po in obstacle_data.get("listOfPoints") or []:
                points.append(PlanPoint(po["x"], po["y"]))
            project.add_obstacle(Obstacle(points))

        return project

    @staticmethod
    def save(project: Project, file_path: str) -> None:
        data = ProjectStore.to_data(project)
        json_text = json.dumps(data)
        Path(file_path).write_text(json_text, encoding="utf-8")

    @staticmethod
    def load(file_path: str) -> Project:
        json_text = Path(file_path).read_text(encoding="utf-8")
        data = json.loads(json_text)
        return ProjectStore.from_data(data)
