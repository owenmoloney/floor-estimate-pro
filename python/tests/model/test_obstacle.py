from floor_estimate_pro.model.obstacle import Obstacle
from floor_estimate_pro.model.plan_point import PlanPoint


def test_rectangle_has_pixel_area_8():
    p0 = PlanPoint(0.0, 0.0)
    p1 = PlanPoint(4.0, 0.0)
    p2 = PlanPoint(4.0, 2.0)
    p3 = PlanPoint(0.0, 2.0)
    obstacle = Obstacle([p0, p1, p2, p3])
    assert abs(obstacle.pixel_area() - 8.0) < 0.0001
