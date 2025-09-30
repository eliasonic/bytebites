package com.bytebites.restaurant_service.service.impl;

import com.bytebites.restaurant_service.dto.RestaurantDto;
import com.bytebites.restaurant_service.entity.Restaurant;
import com.bytebites.restaurant_service.entity.RestaurantOrder;
import com.bytebites.restaurant_service.entity.RestaurantOrder.OrderStatus;
import com.bytebites.restaurant_service.event.OrderPlacedEvent;
import com.bytebites.restaurant_service.mapper.RestaurantMapper;
import com.bytebites.restaurant_service.repository.RestaurantOrderRepository;
import com.bytebites.restaurant_service.repository.RestaurantRepository;
import com.bytebites.restaurant_service.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final RestaurantOrderRepository restaurantOrderRepository;
    private final RestaurantMapper restaurantMapper;

    public List<RestaurantDto> getAllRestaurants() {
        return restaurantRepository.findAll()
                .stream()
                .map(restaurantMapper::toDto)
                .toList();
    }

    public RestaurantDto createRestaurant(RestaurantDto restaurantDto, String ownerId) {
        Restaurant restaurant = restaurantMapper.toEntity(restaurantDto);
        restaurant.setOwnerId(ownerId);
        return restaurantMapper.toDto(restaurantRepository.save(restaurant));
    }

    public RestaurantDto updateRestaurant(Long id, RestaurantDto restaurantDto) {
        Restaurant existing = restaurantRepository.findById(id).orElseThrow();
        restaurantMapper.updateFromDto(restaurantDto, existing);
        return restaurantMapper.toDto(restaurantRepository.save(existing));
    }

    public void startOrderPreparation(OrderPlacedEvent event) {
        boolean exists = restaurantOrderRepository.existsByOrderId(event.orderId());

        if (exists) {
            log.info("Order {} already exists. Skipping duplicate event processing.", event.orderId());
            return;
        }

        RestaurantOrder order = new RestaurantOrder();
        order.setOrderId(event.orderId());
        order.setRestaurantId(event.restaurantId());
        order.setCustomerId(event.customerId());
        order.setStatus(OrderStatus.PREPARING);

        restaurantOrderRepository.save(order);
        log.info("Started preparation for order {} at restaurant {}", event.orderId(), event.restaurantId());
    }

    public boolean isOwner(Long restaurantId, String ownerId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow();
        return restaurant.getOwnerId().equals(ownerId);
    }
}