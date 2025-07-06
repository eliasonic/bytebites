package com.bytebites.restaurant_service.dto;

public record MenuItemDto(
        Long id,
        String name,
        String description,
        Double price,
        Long restaurantId
) {}