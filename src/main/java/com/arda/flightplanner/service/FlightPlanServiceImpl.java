package com.arda.flightplanner.service;

import com.arda.flightplanner.entity.FlightPlan;
import com.arda.flightplanner.repository.FlightPlanRepository;
import com.arda.flightplanner.rest.MaxNumberReachedException;
import com.arda.flightplanner.rest.PlaneIsAlreadyOnAFlightException;
import com.arda.flightplanner.rest.RestServiceException;
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
    public FlightPlan create(FlightPlan plan) throws RestServiceException {
        LockExecutionResult<FlightPlan> result = locker.lock("locked",
                5,
                6,
                () -> {
                    if (repository.countByPlaneIdAndDepartureTimeBeforeAndArrivalTimeAfter(plan.getPlaneId(), plan.getArrivalTime(), plan.getDepartureTime()) > 0) {
                        throw new PlaneIsAlreadyOnAFlightException("plane.already.in.use");
                    }
                    if (repository.countFlight(
                            plan.getAirlineCode(),
                            plan.getDepartureAirportCode(),
                            plan.getDestinationAirport(),
                            plan.getDepartureTime().getDayOfYear(),
                            plan.getArrivalTime().getDayOfYear(),
                            plan.getDepartureTime(),
                            plan.getArrivalTime()
                            ) < MAX_NUMBER_OF_FLIGHT) {
                        return repository.save(plan);
                    } else {
                        throw new MaxNumberReachedException("flightPlanServiceImpl.create.max.number");
                    }
                });
        return result.getResultIfLockAcquired();
    }
}
