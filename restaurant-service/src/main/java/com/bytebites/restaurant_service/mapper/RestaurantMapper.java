package com.bytebites.restaurant_service.mapper;

import com.bytebites.restaurant_service.dto.RestaurantDto;
import com.bytebites.restaurant_service.entity.Restaurant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RestaurantMapper {
    Restaurant toEntity(RestaurantDto dto);
    RestaurantDto toDto(Restaurant entity);

    @Mapping(target = "id", ignore = true)
    void updateFromDto(RestaurantDto dto, @MappingTarget Restaurant entity);
}