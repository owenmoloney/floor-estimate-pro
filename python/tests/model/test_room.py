from floor_estimate_pro.model.plan_point import PlanPoint
from floor_estimate_pro.model.room import Room


def test_rectangle_has_pixel_area_50():
    p0 = PlanPoint(0.0, 0.0)
    p1 = PlanPoint(10.0, 0.0)
    p2 = PlanPoint(10.0, 5.0)
    p3 = PlanPoint(0.0, 5.0)
    room = Room([p0, p1, p2, p3])
    assert abs(room.pixel_area() - 50.0) < 0.0001
