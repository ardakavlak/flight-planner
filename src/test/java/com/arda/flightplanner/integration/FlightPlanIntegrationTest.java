package com.arda.flightplanner.integration;

import com.arda.flightplanner.dto.FlightPlanDTO;
import com.arda.flightplanner.entity.FlightPlan;
import com.arda.flightplanner.repository.FlightPlanRepository;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.jdbc.JdbcTestUtils;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FlightPlanIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private FlightPlanRepository repository;

    @AfterEach
    void tearDown() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "flight_plan");
    }

    @Test
    void givenFlightPlanDTO_whenCreate_ThenReturn210() throws ParseException {

        //given
        var flightPlanDTO = new FlightPlanDTO();
        flightPlanDTO.setAirlineCode("airline1");
        flightPlanDTO.setDepartureAirportCode("airport1");
        flightPlanDTO.setDestinationAirport("airport2");
        String dateString = "2023-03-28T22:30:00.000+0000";
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        Date date = dateFormat.parse(dateString);
        flightPlanDTO.setDepartureTime(date);
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
        flightPlanDTO.setDepartureAirportCode("airport1");
        flightPlanDTO.setDestinationAirport("airport2");
        flightPlanDTO.setDepartureTime(Date.from(Instant.now()));
        flightPlanDTO.setFlightDuration(120);

        var flightPlan1 = new FlightPlan();
        flightPlan1.setAirlineCode("airline1");
        flightPlan1.setDepartureAirportCode("airport1");
        flightPlan1.setDestinationAirport("airport2");
        flightPlan1.setDepartureTime(Date.from(Instant.now()));
        flightPlan1.setFlightDuration(120);
        flightPlan1.setArrivalTime(DateUtil.getDatePlusDuration(Date.from(Instant.now()), 120));

        var flightPlan2 = new FlightPlan();
        flightPlan2.setAirlineCode("airline1");
        flightPlan2.setDepartureAirportCode("airport1");
        flightPlan2.setDestinationAirport("airport2");
        flightPlan2.setDepartureTime(Date.from(Instant.now()));
        flightPlan2.setFlightDuration(120);
        flightPlan2.setArrivalTime(DateUtil.getDatePlusDuration(Date.from(Instant.now()), 120));

        var flightPlan3 = new FlightPlan();
        flightPlan3.setAirlineCode("airline1");
        flightPlan3.setDepartureAirportCode("airport1");
        flightPlan3.setDestinationAirport("airport2");
        flightPlan3.setDepartureTime(Date.from(Instant.now()));
        flightPlan3.setFlightDuration(120);
        flightPlan3.setArrivalTime(DateUtil.getDatePlusDuration(Date.from(Instant.now()), 120));

        repository.saveAll(List.of(flightPlan1, flightPlan2, flightPlan3));


        var httpEntity = new HttpEntity<>(flightPlanDTO);

        //when
        var response = restTemplate.exchange("/api/flight-plans", HttpMethod.POST, httpEntity,
                new ParameterizedTypeReference<>() {
                });

        //then
        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
}
