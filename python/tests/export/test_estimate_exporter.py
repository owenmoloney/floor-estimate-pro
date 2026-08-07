from pathlib import Path

from floor_estimate_pro.export.estimate_exporter import EstimateExporter
from floor_estimate_pro.model.estimate_result import EstimateResult


def test_to_csv_contains_header_and_values():
    result = EstimateResult(42.0, 0.42, 0.462, 2.31)
    csv = EstimateExporter.to_csv(result)
    assert "netPixelArea,netRealArea,materialSqFt,estimatedCost" in csv
    assert "42.0" in csv
    assert "0.42" in csv
    assert "0.462" in csv
    assert "2.31" in csv


def test_export_csv_writes_file(tmp_path: Path):
    result = EstimateResult(42.0, 0.42, 0.462, 2.31)
    path = str(tmp_path / "test-estimate.csv")
    EstimateExporter.export_csv(result, path)
    contents = Path(path).read_text(encoding="utf-8")
    assert contents == EstimateExporter.to_csv(result)
