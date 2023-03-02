package com.arda.flightplanner.controller;

import com.arda.flightplanner.dto.FlightPlanDTO;
import com.arda.flightplanner.dto.FlightPlanResponseDTO;
import com.arda.flightplanner.entity.FlightPlan;
import com.arda.flightplanner.mapper.FlightPlanMapper;
import com.arda.flightplanner.rest.MaxNumberReachedException;
import com.arda.flightplanner.rest.Response;
import com.arda.flightplanner.service.FlightPlanService;
import com.arda.flightplanner.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/flight-plans")
public class FlightPlanController {

    private final FlightPlanService flightPlanService;

    @PostMapping
    public ResponseEntity<Response<FlightPlanResponseDTO>> create(@Valid @RequestBody FlightPlanDTO flightPlanDTO) {
        log.info("post -[{}]", flightPlanDTO);
        flightPlanDTO.setArrivalTime(DateUtil.getDatePlusDuration(flightPlanDTO.getDepartureTime(), flightPlanDTO.getFlightDuration()));
        FlightPlan flightPlan = FlightPlanMapper.INSTANCE.toEntity(flightPlanDTO);
        FlightPlan savedPlan = flightPlanService.create(flightPlan);
        FlightPlanResponseDTO flightPlanResponseDTO = FlightPlanMapper.INSTANCE.toResponseDTO(savedPlan);
        return ResponseEntity.status(HttpStatus.CREATED).body(Response.success(flightPlanResponseDTO));
    }
}
