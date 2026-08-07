from __future__ import annotations

from floor_estimate_pro.model.calibration import Calibration
from floor_estimate_pro.model.obstacle import Obstacle
from floor_estimate_pro.model.room import Room


class Project:
    def __init__(self) -> None:
        self._image_path: str | None = None
        self._calibration: Calibration | None = None
        self._rooms: list[Room] = []
        self._obstacles: list[Obstacle] = []

    def image_path(self) -> str | None:
        return self._image_path

    def calibration(self) -> Calibration | None:
        return self._calibration

    def rooms(self) -> list[Room]:
        return self._rooms

    def obstacles(self) -> list[Obstacle]:
        return self._obstacles

    def set_calibration(self, cal: Calibration) -> None:
        self._calibration = cal

    def add_room(self, room: Room) -> None:
        self._rooms.append(room)

    def add_obstacle(self, obstacle: Obstacle) -> None:
        self._obstacles.append(obstacle)

    def set_image_path(self, path: str | None) -> None:
        self._image_path = path

    def is_calibrated(self) -> bool:
        if self._calibration is not None:
            return True
        else:
            return False

    def room_count(self) -> int:
        return len(self._rooms)

    def obstacle_count(self) -> int:
        return len(self._obstacles)
