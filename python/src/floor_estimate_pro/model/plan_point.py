from __future__ import annotations

from dataclasses import dataclass
import math


@dataclass(frozen=True)
class PlanPoint:
    x: float
    y: float

    def distance_to(self, other: PlanPoint) -> float:
        dx = self.x - other.x
        dy = self.y - other.y
        return math.sqrt(dx * dx + dy * dy)
