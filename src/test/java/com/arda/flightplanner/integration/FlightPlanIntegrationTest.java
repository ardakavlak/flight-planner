package com.arda.flightplanner.integration;

import com.arda.flightplanner.controller.FlightPlanController;
import com.arda.flightplanner.dto.FlightPlanDTO;
import com.arda.flightplanner.dto.FlightPlanResponseDTO;
import com.arda.flightplanner.entity.FlightPlan;
import com.arda.flightplanner.repository.FlightPlanRepository;
import com.arda.flightplanner.rest.Response;
import com.arda.flightplanner.util.DateUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.jdbc.JdbcTestUtils;


import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FlightPlanIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private FlightPlanRepository repository;

    @Autowired
    private FlightPlanController controller;

    @AfterEach
    void tearDown() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "flight_plan");
    }

    @Test
    void givenFlightPlanDTO_whenCreate_ThenReturn210() {

        //given
        var flightPlanDTO = new FlightPlanDTO();
        flightPlanDTO.setAirlineCode("airline1");
        flightPlanDTO.setPlaneId("flight123");
        flightPlanDTO.setDepartureAirportCode("airport1");
        flightPlanDTO.setDestinationAirport("airport2");
        flightPlanDTO.setDepartureTime(LocalDateTime.now());
        flightPlanDTO.setFlightDuration(120);

        var httpEntity = new HttpEntity<>(flightPlanDTO);

        //when
        var response = restTemplate.exchange("/api/flight-plans", HttpMethod.POST, httpEntity,
                new ParameterizedTypeReference<>() {
                });

        //then
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
    }

    @Test
    void givenThreeOngoingFlightPlan_whenCreate_ThenReturn400() throws ParseException {

        //given
        var flightPlanDTO = new FlightPlanDTO();
        flightPlanDTO.setAirlineCode("airline1");
        flightPlanDTO.setPlaneId("flight123");
        flightPlanDTO.setDepartureAirportCode("airport1");
        flightPlanDTO.setDestinationAirport("airport2");
        flightPlanDTO.setDepartureTime(LocalDateTime.now());
        flightPlanDTO.setFlightDuration(120);

        var flightPlan1 = new FlightPlan();
        flightPlan1.setAirlineCode("airline1");
        flightPlan1.setPlaneId("flight123");
        flightPlan1.setDepartureAirportCode("airport1");
        flightPlan1.setDestinationAirport("airport2");
        flightPlan1.setDepartureTime(LocalDateTime.now());
        flightPlan1.setFlightDuration(120);
        flightPlan1.setArrivalTime(DateUtil.getDatePlusDuration(LocalDateTime.now(), 120));

        var flightPlan2 = new FlightPlan();
        flightPlan2.setAirlineCode("airline1");
        flightPlan2.setPlaneId("flight123");
        flightPlan2.setDepartureAirportCode("airport1");
        flightPlan2.setDestinationAirport("airport2");
        flightPlan2.setDepartureTime(LocalDateTime.now());
        flightPlan2.setFlightDuration(120);
        flightPlan2.setArrivalTime(DateUtil.getDatePlusDuration(LocalDateTime.now(), 120));

        var flightPlan3 = new FlightPlan();
        flightPlan3.setAirlineCode("airline1");
        flightPlan3.setPlaneId("flight123");
        flightPlan3.setDepartureAirportCode("airport1");
        flightPlan3.setDestinationAirport("airport2");
        flightPlan3.setDepartureTime(LocalDateTime.now());
        flightPlan3.setFlightDuration(120);
        flightPlan3.setArrivalTime(DateUtil.getDatePlusDuration(LocalDateTime.now(), 120));

        repository.saveAll(List.of(flightPlan1, flightPlan2, flightPlan3));


        var httpEntity = new HttpEntity<>(flightPlanDTO);

        //when
        var response = restTemplate.exchange("/api/flight-plans", HttpMethod.POST, httpEntity,
                new ParameterizedTypeReference<>() {
                });

        //then
        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }


    @Test
    void shouldInsert3WhenTryToCreate4NumberOfFlightsConcurrently() throws InterruptedException {

        var flightPlanDTO1 = new FlightPlanDTO();
        flightPlanDTO1.setAirlineCode("airline1");
        flightPlanDTO1.setPlaneId("flight123");
        flightPlanDTO1.setDepartureAirportCode("airport1");
        flightPlanDTO1.setDestinationAirport("airport2");
        flightPlanDTO1.setDepartureTime(LocalDateTime.now());
        flightPlanDTO1.setFlightDuration(120);

        var flightPlanDTO2 = new FlightPlanDTO();
        flightPlanDTO2.setAirlineCode("airline1");
        flightPlanDTO2.setPlaneId("flight124");
        flightPlanDTO2.setDepartureAirportCode("airport1");
        flightPlanDTO2.setDestinationAirport("airport2");
        flightPlanDTO2.setDepartureTime(LocalDateTime.now());
        flightPlanDTO2.setFlightDuration(120);

        var flightPlanDTO3 = new FlightPlanDTO();
        flightPlanDTO3.setAirlineCode("airline1");
        flightPlanDTO3.setPlaneId("flight125");
        flightPlanDTO3.setDepartureAirportCode("airport1");
        flightPlanDTO3.setDestinationAirport("airport2");
        flightPlanDTO3.setDepartureTime(LocalDateTime.now());
        flightPlanDTO3.setFlightDuration(120);

        var flightPlanDTO4 = new FlightPlanDTO();
        flightPlanDTO4.setAirlineCode("airline1");
        flightPlanDTO4.setPlaneId("flight126");
        flightPlanDTO4.setDepartureAirportCode("airport1");
        flightPlanDTO4.setDestinationAirport("airport2");
        flightPlanDTO4.setDepartureTime(LocalDateTime.now());
        flightPlanDTO4.setFlightDuration(120);

        Executors.newFixedThreadPool(4)
                .invokeAll(Stream.of(flightPlanDTO1, flightPlanDTO2, flightPlanDTO3, flightPlanDTO4)
                        .map(fp -> (Callable<ResponseEntity<Response<FlightPlanResponseDTO>>>) () -> controller.create(fp)).toList());

        Assertions.assertEquals(3, repository.count());
    }


    @Test
    void shouldInsert1WhenTryToCreateMultipleFlightsWithSamePlaneIdConcurrently() throws InterruptedException {

        var flightPlanDTO1 = new FlightPlanDTO();
        flightPlanDTO1.setAirlineCode("airline1");
        flightPlanDTO1.setPlaneId("pl1");
        flightPlanDTO1.setDepartureAirportCode("airport1");
        flightPlanDTO1.setDestinationAirport("airport2");
        flightPlanDTO1.setDepartureTime(LocalDateTime.now());
        flightPlanDTO1.setFlightDuration(120);

        var flightPlanDTO2 = new FlightPlanDTO();
        flightPlanDTO2.setAirlineCode("airline1");
        flightPlanDTO2.setPlaneId("pl1");
        flightPlanDTO2.setDepartureAirportCode("airport1");
        flightPlanDTO2.setDestinationAirport("airport2");
        flightPlanDTO2.setDepartureTime(LocalDateTime.now());
        flightPlanDTO2.setFlightDuration(120);

        var flightPlanDTO3 = new FlightPlanDTO();
        flightPlanDTO3.setAirlineCode("airline1");
        flightPlanDTO3.setPlaneId("pl1");
        flightPlanDTO3.setDepartureAirportCode("airport1");
        flightPlanDTO3.setDestinationAirport("airport2");
        flightPlanDTO3.setDepartureTime(LocalDateTime.now());
        flightPlanDTO3.setFlightDuration(120);

        var flightPlanDTO4 = new FlightPlanDTO();
        flightPlanDTO4.setAirlineCode("airline1");
        flightPlanDTO4.setPlaneId("pl1");
        flightPlanDTO4.setDepartureAirportCode("airport1");
        flightPlanDTO4.setDestinationAirport("airport2");
        flightPlanDTO4.setDepartureTime(LocalDateTime.now());
        flightPlanDTO4.setFlightDuration(120);

        Executors.newFixedThreadPool(4)
                .invokeAll(Stream.of(flightPlanDTO1, flightPlanDTO2, flightPlanDTO3, flightPlanDTO4)
                        .map(fp -> (Callable<ResponseEntity<Response<FlightPlanResponseDTO>>>) () -> controller.create(fp)).toList());

        Assertions.assertEquals(1, repository.count());
    }
}
