from __future__ import annotations

from dataclasses import dataclass

from floor_estimate_pro.model.plan_point import PlanPoint


@dataclass(frozen=True)
class Calibration:
    feet_per_pixel: float

    @staticmethod
    def from_known_length(a: PlanPoint, b: PlanPoint, known_feet: float) -> Calibration:
        pixel_distance = a.distance_to(b)
        if pixel_distance == 0:
            raise ValueError("calibration points must not be the same")
        if known_feet <= 0:
            raise ValueError("known length must be positive")
        scale = known_feet / pixel_distance
        return Calibration(scale)

    def to_feet(self, pixel_length: float) -> float:
        return pixel_length * self.feet_per_pixel

    def to_square_feet(self, pixel_area: float) -> float:
        s = self.feet_per_pixel
        return pixel_area * s * s
