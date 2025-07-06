package com.bytebites.restaurant_service.service;

import com.bytebites.restaurant_service.dto.RestaurantDto;
import com.bytebites.restaurant_service.event.OrderPlacedEvent;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface RestaurantService {
    List<RestaurantDto> getAllRestaurants();

    RestaurantDto createRestaurant(RestaurantDto restaurantDto, String ownerId);

    RestaurantDto updateRestaurant(Long id, RestaurantDto restaurantDto);

    void startOrderPreparation(OrderPlacedEvent event);

    boolean isOwner(Long restaurantId, String ownerId);
}
