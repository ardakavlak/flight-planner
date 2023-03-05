package com.arda.flightplanner.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
public class FlightPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "plan_gen")
    @SequenceGenerator(name = "plan_gen", sequenceName = "plan_seq")
    private Long id;

    private String airlineCode;

    private String departureAirportCode;

    private String destinationAirport;

    private LocalDateTime departureTime;

    private LocalDateTime arrivalTime;

    private int flightDuration;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", insertable = false)
    private Date updatedAt;
}
