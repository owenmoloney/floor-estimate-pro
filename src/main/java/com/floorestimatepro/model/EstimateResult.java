package com.floorestimatepro.model;

public record EstimateResult(
    double netPixelArea,
    double netRealArea,
    double materialSqFt,
    double estimatedCost
){};