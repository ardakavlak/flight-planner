package com.arda.flightplanner.mapper;

import com.arda.flightplanner.dto.FlightPlanDTO;
import com.arda.flightplanner.dto.FlightPlanResponseDTO;
import com.arda.flightplanner.entity.FlightPlan;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FlightPlanMapper {
    FlightPlanMapper INSTANCE = Mappers.getMapper(FlightPlanMapper.class);

    FlightPlanResponseDTO toResponseDTO(FlightPlan entity);

    @InheritInverseConfiguration
    FlightPlan toEntity(FlightPlanDTO dto);

}
