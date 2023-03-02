package com.arda.flightplanner.repository;

import com.arda.flightplanner.entity.FlightPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface FlightPlanRepository extends JpaRepository<FlightPlan, Long> {

    long countByAirlineCodeAndDepartureTimeBeforeAndArrivalTimeAfter(String airwayCode, Date arrivalTime, Date departureTime);

}
