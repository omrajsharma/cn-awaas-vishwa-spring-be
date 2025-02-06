package com.example.awaas.mappers;

import com.example.awaas.dtos.PropertyDTO;
import com.example.awaas.entities.Property;
import com.example.awaas.projections.PropertyProjection;
import com.example.awaas.requests.CreatePropertyRequest;
import com.example.awaas.response.PropertyProjectionResponse;
import com.example.awaas.response.PropertyResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper
public interface PropertyMapper {
    PropertyMapper INSTANCE = Mappers.getMapper(PropertyMapper.class);

    Property toEntity(PropertyDTO dto);

    PropertyDTO toPropertyDTO(Property property);

    PropertyDTO toPropertyDTO(CreatePropertyRequest createPropertyRequest);

    PropertyResponse toPropertyResponse(PropertyDTO propertyDTO);

    // Custom method to map Page<Property> to Page<PropertyResponse>
    default Page<PropertyResponse> toPagePropertyResponse(Page<Property> propertyPage) {
        return propertyPage.map(this::toPropertyResponseFromEntity);
    }

    // Add method to map Property entity directly to PropertyResponse
    PropertyResponse toPropertyResponseFromEntity(Property property);

    List<PropertyDTO> toPropertyDTOList(List<Property> propertyList);

    List<PropertyResponse> toPropertyResponseList(List<PropertyDTO> properties);

    // projection method
    PropertyProjectionResponse toResponse(PropertyProjection projection);

    default Page<PropertyProjectionResponse> toPagePropertyProjectionResponse(
            Page<PropertyProjection> propertyProjection) {
        return propertyProjection.map(this::toResponse);
    }
}
