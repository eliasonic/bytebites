package com.bytebites.restaurant_service.mapper;

import com.bytebites.restaurant_service.dto.MenuItemDto;
import com.bytebites.restaurant_service.entity.MenuItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MenuItemMapper {
    @Mapping(target = "restaurant.id", source = "restaurantId")
    MenuItem toEntity(MenuItemDto dto);

    @Mapping(target = "restaurantId", source = "restaurant.id")
    MenuItemDto toDto(MenuItem entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "restaurant", ignore = true)
    void updateFromDto(MenuItemDto dto, @MappingTarget MenuItem entity);
}