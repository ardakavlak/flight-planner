package com.arda.flightplanner.repository;

import com.arda.flightplanner.entity.FlightPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;


public interface FlightPlanRepository extends JpaRepository<FlightPlan, Long> {

    @Query("SELECT count(*) FROM FlightPlan f " +
            "WHERE f.airlineCode = :airlineCode " +
            "AND f.departureAirportCode = :departureAirportCode " +
            "AND f.destinationAirport = :destinationAirport " +
            "AND " +
            "(FUNCTION('DATE_PART', 'doy', f.departureTime) = :departureTimeDay " +
            "OR FUNCTION('DATE_PART', 'doy', f.departureTime) = :arrivalTimeDay " +
            "OR FUNCTION('DATE_PART', 'doy', f.arrivalTime) = :departureTimeDay " +
            "OR FUNCTION('DATE_PART', 'doy', f.arrivalTime) = :arrivalTimeDay)" +
            "AND f.arrivalTime > :departureTime")
    long countFlight(
            @Param("airlineCode") String airlineCode,
            @Param("departureAirportCode") String departureAirportCode,
            @Param("destinationAirport") String destinationAirport,
            @Param("departureTimeDay") int departureTimeDay,
            @Param("arrivalTimeDay") int arrivalTimeDay,
            @Param("departureTime") LocalDateTime departureTime);

}
