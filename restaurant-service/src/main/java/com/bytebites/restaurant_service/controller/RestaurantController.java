package com.bytebites.restaurant_service.controller;

import com.bytebites.restaurant_service.dto.RestaurantDto;
import com.bytebites.restaurant_service.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/restaurants")
@RequiredArgsConstructor
public class RestaurantController {
    private final RestaurantService restaurantService;

    @GetMapping
    public List<RestaurantDto> getAllRestaurants() {
        return restaurantService.getAllRestaurants();
    }

    @PostMapping
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public RestaurantDto createRestaurant(@RequestBody RestaurantDto restaurantDto, @AuthenticationPrincipal String ownerId) {
        return restaurantService.createRestaurant(restaurantDto, ownerId);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('RESTAURANT_OWNER') and @restaurantServiceImpl.isOwner(#id, authentication.principal)")
    public RestaurantDto updateRestaurant(@PathVariable Long id, @RequestBody RestaurantDto restaurantDto) {
        return restaurantService.updateRestaurant(id, restaurantDto);
    }
}