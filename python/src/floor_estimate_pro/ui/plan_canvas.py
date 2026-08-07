from __future__ import annotations

from PySide6.QtCore import Qt, Signal
from PySide6.QtGui import QColor, QImage, QPainter, QPen, QWheelEvent
from PySide6.QtWidgets import QWidget

from floor_estimate_pro.model.obstacle import Obstacle
from floor_estimate_pro.model.plan_point import PlanPoint
from floor_estimate_pro.model.project import Project
from floor_estimate_pro.model.room import Room
from floor_estimate_pro.ui.tool_mode import ToolMode


class PlanCanvas(QWidget):
    changed = Signal()

    def __init__(self, project: Project) -> None:
        super().__init__()
        self.project = project
        self.tool_mode = ToolMode.NONE
        self.plan_image: QImage | None = None
        self.pending_pts: list[PlanPoint] = []
        self.zoom = 1.0
        self.setMinimumSize(900, 600)
        self.setStyleSheet("background-color: #404040;")
        self.setMouseTracking(True)

    def set_project(self, project: Project) -> None:
        self.project = project
        self.pending_pts.clear()
        self.load_image_from_project()
        self.update()

    def get_project(self) -> Project:
        return self.project

    def set_tool_mode(self, mode: ToolMode) -> None:
        self.tool_mode = mode
        self.pending_pts.clear()
        self.update()

    def get_tool_mode(self) -> ToolMode:
        return self.tool_mode

    def get_zoom(self) -> float:
        return self.zoom

    def zoom_in(self) -> None:
        self.set_zoom(self.zoom * 1.25)

    def zoom_out(self) -> None:
        self.set_zoom(self.zoom / 1.25)

    def reset_zoom(self) -> None:
        self.set_zoom(1.0)

    def set_zoom(self, new_zoom: float) -> None:
        if new_zoom < 0.25:
            new_zoom = 0.25
        if new_zoom > 8.0:
            new_zoom = 8.0
        self.zoom = new_zoom
        self.update_canvas_size()
        self.update()
        self.changed.emit()

    def load_image_from_project(self) -> None:
        try:
            if self.project.image_path() is None:
                self.plan_image = None
                self.update()
                return
            self.plan_image = QImage(self.project.image_path())
            if self.plan_image.isNull():
                self.plan_image = None
            self.update_canvas_size()
        except Exception:
            self.plan_image = None
        self.update()

    def update_canvas_size(self) -> None:
        if self.plan_image is not None:
            w = int(self.plan_image.width() * self.zoom)
            h = int(self.plan_image.height() * self.zoom)
            self.setMinimumSize(w, h)
            self.resize(w, h)
        else:
            self.setMinimumSize(int(900 * self.zoom), int(600 * self.zoom))

    def finish_room(self) -> None:
        if len(self.pending_pts) < 3:
            raise ValueError("Need at least 3 corners")
        self.project.add_room(Room(list(self.pending_pts)))
        self.pending_pts.clear()
        self.update()
        self.changed.emit()

    def finish_obstacle(self) -> None:
        if len(self.pending_pts) < 3:
            raise ValueError("Need at least 3 corners")
        self.project.add_obstacle(Obstacle(list(self.pending_pts)))
        self.pending_pts.clear()
        self.update()
        self.changed.emit()

    def mousePressEvent(self, event) -> None:
        if event.button() != Qt.LeftButton:
            return
        self.on_mouse_click(event.position().x(), event.position().y())

    def wheelEvent(self, event: QWheelEvent) -> None:
        delta = event.angleDelta().y()
        if delta > 0:
            self.zoom_in()
        elif delta < 0:
            self.zoom_out()

    def on_mouse_click(self, screen_x: float, screen_y: float) -> None:
        image_x = screen_x / self.zoom
        image_y = screen_y / self.zoom
        point = PlanPoint(image_x, image_y)

        if self.tool_mode == ToolMode.CALIBRATE:
            self.pending_pts.append(point)
        elif self.tool_mode == ToolMode.DRAW_ROOM:
            self.pending_pts.append(point)
        elif self.tool_mode == ToolMode.DRAW_OBSTACLE:
            self.pending_pts.append(point)

        self.update()
        self.changed.emit()

    def paintEvent(self, event) -> None:
        painter = QPainter(self)
        painter.fillRect(self.rect(), QColor(64, 64, 64))
        painter.scale(self.zoom, self.zoom)

        if self.plan_image is not None:
            painter.drawImage(0, 0, self.plan_image)

        pen = QPen(QColor(30, 144, 255, 180))
        pen.setWidthF(max(1.0, 2.0 / self.zoom))
        painter.setPen(pen)
        for room in self.project.rooms():
            self.draw_polygon(painter, room.list_of_points, True)

        pen = QPen(QColor(220, 20, 60, 180))
        pen.setWidthF(max(1.0, 2.0 / self.zoom))
        painter.setPen(pen)
        for obstacle in self.project.obstacles():
            self.draw_polygon(painter, obstacle.list_of_points, True)

        pen = QPen(QColor(255, 255, 0))
        pen.setWidthF(max(1.0, 2.0 / self.zoom))
        painter.setPen(pen)
        self.draw_polygon(painter, self.pending_pts, False)
        dot = max(2, int(4 / self.zoom))
        painter.setBrush(QColor(255, 255, 0))
        for p in self.pending_pts:
            painter.drawEllipse(int(p.x) - dot, int(p.y) - dot, dot * 2, dot * 2)

    def draw_polygon(self, painter: QPainter, points: list[PlanPoint], closed: bool) -> None:
        if points is None or len(points) < 2:
            return
        for i in range(len(points) - 1):
            a = points[i]
            b = points[i + 1]
            painter.drawLine(int(a.x), int(a.y), int(b.x), int(b.y))
        if closed and len(points) >= 3:
            first = points[0]
            last = points[-1]
            painter.drawLine(int(last.x), int(last.y), int(first.x), int(first.y))
