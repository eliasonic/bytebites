package com.bytebites.order_service.repository;

import com.bytebites.order_service.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerId(String customerId);

    List<Order> findByRestaurantId(Long restaurantId);
}