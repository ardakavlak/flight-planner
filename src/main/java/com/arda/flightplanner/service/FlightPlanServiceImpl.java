package com.arda.flightplanner.service;

import com.arda.flightplanner.entity.FlightPlan;
import com.arda.flightplanner.repository.FlightPlanRepository;
import com.arda.flightplanner.rest.MaxNumberReachedException;
import com.arda.flightplanner.service.locker.DistributedLocker;
import com.arda.flightplanner.service.locker.LockExecutionResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Slf4j
@Service
public class FlightPlanServiceImpl implements FlightPlanService {

    private final DistributedLocker locker;

    private final FlightPlanRepository repository;

    private static final long MAX_NUMBER_OF_FLIGHT = 3L;

    @Override
    public FlightPlan create(FlightPlan plan) {
        if (repository.countByAirlineCodeAndDepartureTimeBeforeAndArrivalTimeAfter(
                plan.getAirlineCode(), plan.getArrivalTime(),
                plan.getDepartureTime()) < MAX_NUMBER_OF_FLIGHT) {
                LockExecutionResult<FlightPlan> result = locker.lock("locked",
                    5,
                    6,
                    () -> repository.save(plan));
            return result.getResultIfLockAcquired();
        } else {
            throw new MaxNumberReachedException("flightPlanServiceImpl.create.max.number");
        }
    }
}
