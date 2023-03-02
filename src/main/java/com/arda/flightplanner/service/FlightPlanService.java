package com.arda.flightplanner.service;

import com.arda.flightplanner.entity.FlightPlan;
import com.arda.flightplanner.rest.RestServiceException;

public interface FlightPlanService {
    FlightPlan create(FlightPlan plan) throws RestServiceException;
}
