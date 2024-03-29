package com.arda.flightplanner.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
public class FlightPlanDTO {

    @NotNull(message = "flightPlanDTO.planeId.invalidOrEmpty")
    private String planeId;

    @NotNull(message = "flightPlanDTO.airlineCode.invalidOrEmpty")
    private String airlineCode;

    @NotNull(message = "flightPlanDTO.departureAirportCode.invalidOrEmpty")
    private String departureAirportCode;

    @NotNull(message = "flightPlanDTO.destinationAirport.invalidOrEmpty")
    private String destinationAirport;

    @NotNull(message = "flightPlanDTO.departureTime.invalidOrEmpty")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime departureTime;

    private LocalDateTime arrivalTime;

    @NotNull(message = "flightPlanDTO.flightDuration.invalidOrEmpty")
    private int flightDuration;

}
