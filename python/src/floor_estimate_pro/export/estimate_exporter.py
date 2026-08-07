from __future__ import annotations

from pathlib import Path

from floor_estimate_pro.model.estimate_result import EstimateResult


class EstimateExporter:
    @staticmethod
    def to_csv(result: EstimateResult) -> str:
        return (
            "netPixelArea,netRealArea,materialSqFt,estimatedCost\n"
            + str(result.net_pixel_area) + ","
            + str(result.net_real_area) + ","
            + str(result.material_sq_ft) + ","
            + str(result.estimated_cost) + "\n"
        )

    @staticmethod
    def export_csv(result: EstimateResult, file_path: str) -> None:
        csv = EstimateExporter.to_csv(result)
        Path(file_path).write_text(csv, encoding="utf-8")
