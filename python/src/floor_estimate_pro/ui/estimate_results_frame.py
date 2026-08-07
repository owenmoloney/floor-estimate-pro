from __future__ import annotations

from PySide6.QtGui import QFont
from PySide6.QtWidgets import (
    QDialog,
    QFileDialog,
    QHBoxLayout,
    QLabel,
    QMessageBox,
    QPushButton,
    QVBoxLayout,
)

from floor_estimate_pro.export.estimate_exporter import EstimateExporter
from floor_estimate_pro.model.estimate_result import EstimateResult


class EstimateResultsFrame(QDialog):
    def __init__(self, result: EstimateResult, room_count: int, obstacle_count: int) -> None:
        super().__init__()
        self.result = result
        self.setWindowTitle("Finished Estimate")

        layout = QVBoxLayout(self)
        title = QLabel("Estimate Results")
        title_font = QFont()
        title_font.setBold(True)
        title_font.setPointSize(18)
        title.setFont(title_font)
        layout.addWidget(title)
        layout.addWidget(QLabel("Rooms: " + str(room_count)))
        layout.addWidget(QLabel("Obstacles: " + str(obstacle_count)))
        layout.addWidget(QLabel("Net pixel area: " + str(self.round(result.net_pixel_area))))
        layout.addWidget(QLabel("Net area (sq ft): " + str(self.round(result.net_real_area))))
        layout.addWidget(QLabel("Material (sq ft): " + str(self.round(result.material_sq_ft))))
        layout.addWidget(QLabel("Estimated cost: $" + str(self.round(result.estimated_cost))))

        buttons = QHBoxLayout()
        export_btn = QPushButton("Export CSV")
        close_btn = QPushButton("Close")
        buttons.addWidget(export_btn)
        buttons.addWidget(close_btn)
        layout.addLayout(buttons)

        export_btn.clicked.connect(self.on_export)
        close_btn.clicked.connect(self.accept)

    def on_export(self) -> None:
        path, _ = QFileDialog.getSaveFileName(self, "Export CSV", "", "CSV (*.csv)")
        if not path:
            return
        try:
            if not path.endswith(".csv"):
                path = path + ".csv"
            EstimateExporter.export_csv(self.result, path)
            QMessageBox.information(self, "Export", "Exported " + path)
        except Exception as ex:
            QMessageBox.warning(self, "Error", str(ex))

    def round(self, value: float) -> float:
        return round(value * 1000.0) / 1000.0
