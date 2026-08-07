from floor_estimate_pro.model.calibration import Calibration
from floor_estimate_pro.model.estimate_calculator import EstimateCalculator
from floor_estimate_pro.model.obstacle import Obstacle
from floor_estimate_pro.model.plan_point import PlanPoint
from floor_estimate_pro.model.room import Room


def test_room_minus_one_obstacle_then_scale_and_waste():
    room = Room([
        PlanPoint(0, 0), PlanPoint(10, 0),
        PlanPoint(10, 5), PlanPoint(0, 5),
    ])
    obstacle = Obstacle([
        PlanPoint(0, 0), PlanPoint(4, 0),
        PlanPoint(4, 2), PlanPoint(0, 2),
    ])
    cal = Calibration(0.1)
    result = EstimateCalculator.calculate([room], [obstacle], cal, 1.10, 5.0)
    assert abs(result.net_pixel_area - 42.0) < 0.0001
    assert abs(result.net_real_area - 0.42) < 0.0001
    assert abs(result.material_sq_ft - 0.462) < 0.0001
    assert abs(result.estimated_cost - 2.31) < 0.0001


def test_two_rooms_minus_one_obstacle_then_scale_and_waste():
    room1 = Room([
        PlanPoint(0, 0), PlanPoint(10, 0),
        PlanPoint(10, 5), PlanPoint(0, 5),
    ])
    room2 = Room([
        PlanPoint(0, 0), PlanPoint(10, 0),
        PlanPoint(10, 5), PlanPoint(0, 5),
    ])
    obstacle = Obstacle([
        PlanPoint(0, 0), PlanPoint(4, 0),
        PlanPoint(4, 2), PlanPoint(0, 2),
    ])
    cal = Calibration(0.1)
    result = EstimateCalculator.calculate(
        [room1, room2], [obstacle], cal, 1.10, 5.0
    )
    assert abs(result.net_pixel_area - 92.0) < 0.0001
    assert abs(result.net_real_area - 0.92) < 0.0001
    assert abs(result.material_sq_ft - 1.012) < 0.0001
    assert abs(result.estimated_cost - 5.06) < 0.0001
