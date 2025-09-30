package com.bytebites.restaurant_service.repository;

import com.bytebites.restaurant_service.entity.RestaurantOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantOrderRepository extends JpaRepository<RestaurantOrder, Long> {
    boolean existsByOrderId(Long orderId);
}
