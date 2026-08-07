from __future__ import annotations

from floor_estimate_pro.model.calibration import Calibration
from floor_estimate_pro.model.estimate_result import EstimateResult
from floor_estimate_pro.model.obstacle import Obstacle
from floor_estimate_pro.model.room import Room


class EstimateCalculator:
    @staticmethod
    def calculate(
        rooms: list[Room],
        obstacles: list[Obstacle],
        calibration: Calibration,
        waste_factor: float,
        price_per_sq_ft: float,
    ) -> EstimateResult:
        room_area = 0.0
        for room in rooms:
            room_area = room_area + room.pixel_area()

        obstacle_total = 0.0
        for obstacle in obstacles:
            obstacle_total = obstacle_total + obstacle.pixel_area()

        net_pixel_area = room_area - obstacle_total

        if net_pixel_area < 0:
            raise ValueError("obstacles larger than room")

        net_real_area = calibration.to_square_feet(net_pixel_area)

        material_sq_ft = net_real_area * waste_factor

        estimated_cost = material_sq_ft * price_per_sq_ft

        return EstimateResult(
            net_pixel_area,
            net_real_area,
            material_sq_ft,
            estimated_cost,
        )
