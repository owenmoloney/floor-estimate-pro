from __future__ import annotations

from dataclasses import dataclass


@dataclass(frozen=True)
class EstimateResult:
    net_pixel_area: float
    net_real_area: float
    material_sq_ft: float
    estimated_cost: float
