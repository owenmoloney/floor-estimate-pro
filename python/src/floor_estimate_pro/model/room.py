from __future__ import annotations

from dataclasses import dataclass

from floor_estimate_pro.model.calibration import Calibration
from floor_estimate_pro.model.plan_point import PlanPoint


@dataclass(frozen=True)
class Room:
    list_of_points: list[PlanPoint]

    def pixel_area(self) -> float:
        n = len(self.list_of_points)
        if n < 3:
            raise ValueError("A room needs at least 3 points")
        sum1 = 0.0
        sum2 = 0.0
        for i in range(n):
            current = self.list_of_points[i]
            nxt = self.list_of_points[(i + 1) % n]
            sum1 = sum1 + (current.x * nxt.y)
            sum2 = sum2 + (current.y * nxt.x)
        area = abs(sum1 - sum2) / 2
        return area

    def area_in_square_feet(self, cal: Calibration) -> float:
        pixel_area = self.pixel_area()
        return cal.to_square_feet(pixel_area)
