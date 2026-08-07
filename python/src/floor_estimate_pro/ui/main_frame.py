from __future__ import annotations

from PySide6.QtWidgets import (
    QFileDialog,
    QHBoxLayout,
    QLabel,
    QLineEdit,
    QMainWindow,
    QMessageBox,
    QScrollArea,
    QVBoxLayout,
    QWidget,
    QPushButton,
)

from floor_estimate_pro.export.estimate_exporter import EstimateExporter
from floor_estimate_pro.model.calibration import Calibration
from floor_estimate_pro.model.estimate_calculator import EstimateCalculator
from floor_estimate_pro.model.project import Project
from floor_estimate_pro.persistence.project_store import ProjectStore
from floor_estimate_pro.ui.estimate_results_frame import EstimateResultsFrame
from floor_estimate_pro.ui.plan_canvas import PlanCanvas
from floor_estimate_pro.ui.tool_mode import ToolMode


class MainFrame(QMainWindow):
    def __init__(self) -> None:
        super().__init__()
        self.project = Project()
        self.canvas = PlanCanvas(self.project)
        self.known_feet_field = QLineEdit("10")
        self.known_feet_field.setFixedWidth(50)
        self.waste_field = QLineEdit("1.10")
        self.waste_field.setFixedWidth(50)
        self.price_field = QLineEdit("5.0")
        self.price_field.setFixedWidth(50)
        self.status_label = QLabel("Open a plan image to start")
        self.result_label = QLabel("Estimate: —")

        self.setWindowTitle("Floor Estimate Pro")

        open_btn = QPushButton("Open Image")
        calibrate_btn = QPushButton("Calibrate")
        apply_cal_btn = QPushButton("Apply Calibration")
        room_btn = QPushButton("Draw Room")
        finish_room_btn = QPushButton("Finish Room")
        obstacle_btn = QPushButton("Draw Obstacle")
        finish_obstacle_btn = QPushButton("Finish Obstacle")
        estimate_btn = QPushButton("Estimate")
        finish_estimate_btn = QPushButton("Finish Estimate")
        save_btn = QPushButton("Save")
        load_btn = QPushButton("Load")
        export_btn = QPushButton("Export")
        zoom_in_btn = QPushButton("Zoom In")
        zoom_out_btn = QPushButton("Zoom Out")
        zoom_reset_btn = QPushButton("Zoom 100%")

        toolbar = QHBoxLayout()
        toolbar.addWidget(open_btn)
        toolbar.addWidget(zoom_in_btn)
        toolbar.addWidget(zoom_out_btn)
        toolbar.addWidget(zoom_reset_btn)
        toolbar.addWidget(calibrate_btn)
        toolbar.addWidget(QLabel("Feet:"))
        toolbar.addWidget(self.known_feet_field)
        toolbar.addWidget(apply_cal_btn)
        toolbar.addWidget(room_btn)
        toolbar.addWidget(finish_room_btn)
        toolbar.addWidget(obstacle_btn)
        toolbar.addWidget(finish_obstacle_btn)
        toolbar.addStretch()

        toolbar2 = QHBoxLayout()
        toolbar2.addWidget(QLabel("Waste:"))
        toolbar2.addWidget(self.waste_field)
        toolbar2.addWidget(QLabel("Price:"))
        toolbar2.addWidget(self.price_field)
        toolbar2.addWidget(estimate_btn)
        toolbar2.addWidget(finish_estimate_btn)
        toolbar2.addWidget(save_btn)
        toolbar2.addWidget(load_btn)
        toolbar2.addWidget(export_btn)
        toolbar2.addStretch()

        south = QHBoxLayout()
        south.addWidget(self.status_label)
        south.addStretch()
        south.addWidget(self.result_label)

        scroll = QScrollArea()
        scroll.setWidget(self.canvas)
        scroll.setWidgetResizable(False)

        central = QWidget()
        root = QVBoxLayout(central)
        root.addLayout(toolbar)
        root.addLayout(toolbar2)
        root.addWidget(scroll, 1)
        root.addLayout(south)
        self.setCentralWidget(central)

        self.canvas.changed.connect(self.refresh_status)

        open_btn.clicked.connect(self.on_open_image)
        zoom_in_btn.clicked.connect(self.canvas.zoom_in)
        zoom_out_btn.clicked.connect(self.canvas.zoom_out)
        zoom_reset_btn.clicked.connect(self.canvas.reset_zoom)
        calibrate_btn.clicked.connect(self.on_calibrate_tool)
        apply_cal_btn.clicked.connect(self.on_apply_calibration)
        room_btn.clicked.connect(self.on_draw_room_tool)
        finish_room_btn.clicked.connect(self.on_finish_room)
        obstacle_btn.clicked.connect(self.on_draw_obstacle_tool)
        finish_obstacle_btn.clicked.connect(self.on_finish_obstacle)
        estimate_btn.clicked.connect(self.refresh_estimate)
        finish_estimate_btn.clicked.connect(self.on_finish_estimate)
        save_btn.clicked.connect(self.on_save)
        load_btn.clicked.connect(self.on_load)
        export_btn.clicked.connect(self.on_export)

        self.resize(1100, 750)

    def on_open_image(self) -> None:
        path, _ = QFileDialog.getOpenFileName(
            self, "Open Image", "", "Images (*.png *.jpg *.jpeg *.gif)"
        )
        if not path:
            return
        self.project.set_image_path(path)
        self.canvas.load_image_from_project()
        self.status_label.setText("Loaded " + path.split("/")[-1].split("\\")[-1])

    def on_calibrate_tool(self) -> None:
        self.canvas.set_tool_mode(ToolMode.CALIBRATE)
        self.status_label.setText(
            "Click two ends of a known length, enter feet, then Apply Calibration"
        )

    def on_apply_calibration(self) -> None:
        if len(self.canvas.pending_pts) < 2:
            QMessageBox.warning(self, "Calibration", "Click two calibration points first")
            return
        try:
            known_feet = float(self.known_feet_field.text().strip())
            a = self.canvas.pending_pts[0]
            b = self.canvas.pending_pts[1]
            cal = Calibration.from_known_length(a, b, known_feet)
            self.project.set_calibration(cal)
            self.canvas.pending_pts.clear()
            self.canvas.update()
            self.status_label.setText("Calibrated: " + str(cal.feet_per_pixel) + " ft/px")
            self.refresh_estimate()
        except Exception as ex:
            QMessageBox.warning(self, "Error", str(ex))

    def on_draw_room_tool(self) -> None:
        self.canvas.set_tool_mode(ToolMode.DRAW_ROOM)
        self.status_label.setText("Click room corners, then Finish Room")

    def on_finish_room(self) -> None:
        try:
            self.canvas.finish_room()
            self.status_label.setText("Room added (" + str(self.project.room_count()) + ")")
            self.refresh_estimate()
        except Exception as ex:
            QMessageBox.warning(self, "Error", str(ex))

    def on_draw_obstacle_tool(self) -> None:
        self.canvas.set_tool_mode(ToolMode.DRAW_OBSTACLE)
        self.status_label.setText("Click obstacle corners, then Finish Obstacle")

    def on_finish_obstacle(self) -> None:
        try:
            self.canvas.finish_obstacle()
            self.status_label.setText(
                "Obstacle added (" + str(self.project.obstacle_count()) + ")"
            )
            self.refresh_estimate()
        except Exception as ex:
            QMessageBox.warning(self, "Error", str(ex))

    def refresh_status(self) -> None:
        zoom_text = " | Zoom: " + str(round(self.canvas.get_zoom() * 100)) + "%"
        if self.canvas.get_tool_mode() == ToolMode.CALIBRATE:
            self.status_label.setText(
                "Calibration clicks: " + str(len(self.canvas.pending_pts)) + "/2" + zoom_text
            )
        elif self.canvas.get_tool_mode() == ToolMode.DRAW_ROOM:
            self.status_label.setText(
                "Room corners: " + str(len(self.canvas.pending_pts)) + zoom_text
            )
        elif self.canvas.get_tool_mode() == ToolMode.DRAW_OBSTACLE:
            self.status_label.setText(
                "Obstacle corners: " + str(len(self.canvas.pending_pts)) + zoom_text
            )
        else:
            self.status_label.setText("Zoom: " + str(round(self.canvas.get_zoom() * 100)) + "%")

    def refresh_estimate(self) -> None:
        if not self.project.is_calibrated():
            self.result_label.setText("Estimate: calibrate first")
            return
        if len(self.project.rooms()) == 0:
            self.result_label.setText("Estimate: add a room first")
            return
        try:
            waste = float(self.waste_field.text().strip())
            price = float(self.price_field.text().strip())
            result = EstimateCalculator.calculate(
                self.project.rooms(),
                self.project.obstacles(),
                self.project.calibration(),
                waste,
                price,
            )
            self.result_label.setText(
                "Net px2: " + str(self.round(result.net_pixel_area))
                + " | Sq ft: " + str(self.round(result.net_real_area))
                + " | Material: " + str(self.round(result.material_sq_ft))
                + " | Cost: $" + str(self.round(result.estimated_cost))
            )
        except Exception as ex:
            self.result_label.setText("Estimate error: " + str(ex))

    def on_save(self) -> None:
        path, _ = QFileDialog.getSaveFileName(self, "Save", "", "JSON (*.json)")
        if not path:
            return
        try:
            if not path.endswith(".json"):
                path = path + ".json"
            ProjectStore.save(self.project, path)
            self.status_label.setText("Saved " + path)
        except Exception as ex:
            QMessageBox.warning(self, "Error", str(ex))

    def on_load(self) -> None:
        path, _ = QFileDialog.getOpenFileName(self, "Load", "", "JSON (*.json)")
        if not path:
            return
        try:
            self.project = ProjectStore.load(path)
            self.canvas.set_project(self.project)
            self.status_label.setText("Loaded project")
            self.refresh_estimate()
        except Exception as ex:
            QMessageBox.warning(self, "Error", str(ex))

    def on_export(self) -> None:
        if not self.project.is_calibrated() or len(self.project.rooms()) == 0:
            QMessageBox.warning(self, "Export", "Calibrate and add a room before exporting")
            return
        path, _ = QFileDialog.getSaveFileName(self, "Export CSV", "", "CSV (*.csv)")
        if not path:
            return
        try:
            waste = float(self.waste_field.text().strip())
            price = float(self.price_field.text().strip())
            result = EstimateCalculator.calculate(
                self.project.rooms(),
                self.project.obstacles(),
                self.project.calibration(),
                waste,
                price,
            )
            if not path.endswith(".csv"):
                path = path + ".csv"
            EstimateExporter.export_csv(result, path)
            self.status_label.setText("Exported " + path)
        except Exception as ex:
            QMessageBox.warning(self, "Error", str(ex))

    def on_finish_estimate(self) -> None:
        if not self.project.is_calibrated():
            QMessageBox.warning(self, "Estimate", "Calibrate first")
            return
        if len(self.project.rooms()) == 0:
            QMessageBox.warning(self, "Estimate", "Add a room first")
            return
        try:
            waste = float(self.waste_field.text().strip())
            price = float(self.price_field.text().strip())
            result = EstimateCalculator.calculate(
                self.project.rooms(),
                self.project.obstacles(),
                self.project.calibration(),
                waste,
                price,
            )
            frame = EstimateResultsFrame(
                result,
                self.project.room_count(),
                self.project.obstacle_count(),
            )
            frame.exec()
        except Exception as ex:
            QMessageBox.warning(self, "Error", str(ex))

    def round(self, value: float) -> float:
        return round(value * 1000.0) / 1000.0
