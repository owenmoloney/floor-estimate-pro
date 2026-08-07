from floor_estimate_pro.model.calibration import Calibration
from floor_estimate_pro.model.plan_point import PlanPoint


def test_from_known_length_100_pixels_and_10_feet_gives_0_1_ft_per_px():
    a = PlanPoint(0.0, 0.0)
    b = PlanPoint(100.0, 0.0)
    known_feet = 10
    cal = Calibration.from_known_length(a, b, known_feet)
    assert abs(cal.feet_per_pixel - 0.1) < 0.0001
