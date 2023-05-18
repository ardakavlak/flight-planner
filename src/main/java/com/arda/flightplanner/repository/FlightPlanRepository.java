package com.arda.flightplanner.repository;

import com.arda.flightplanner.entity.FlightPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;


public interface FlightPlanRepository extends JpaRepository<FlightPlan, Long> {

    @Query("SELECT count(*) FROM FlightPlan f " +
            "WHERE f.airlineCode = :airlineCode " +
            "AND FUNCTION('DATE_PART', 'doy', f.departureTime) = :departureTimeDay ")
    long countFlight(
            @Param("airlineCode") String airlineCode,
            @Param("departureTimeDay") int departureTimeDay);

    long countByPlaneIdAndDepartureTimeBeforeAndArrivalTimeAfter(String planeId, LocalDateTime arrivalTime, LocalDateTime departureTime);

}
