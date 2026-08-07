from floor_estimate_pro.model.plan_point import PlanPoint


def test_distance_of_3_4_5_triangle_is_5():
    origin = PlanPoint(0.0, 0.0)
    corner = PlanPoint(3.0, 4.0)
    actual = origin.distance_to(corner)
    assert abs(actual - 5.0) < 0.0001
