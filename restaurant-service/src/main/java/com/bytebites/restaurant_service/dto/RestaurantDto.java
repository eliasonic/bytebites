package com.bytebites.restaurant_service.dto;

import java.util.List;

public record RestaurantDto(
        Long id,
        String name,
        String address,
        String ownerId
) {}