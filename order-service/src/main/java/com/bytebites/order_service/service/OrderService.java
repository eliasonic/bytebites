package com.bytebites.order_service.service;

import com.bytebites.order_service.dto.OrderDto;

import java.util.List;

public interface OrderService {
    OrderDto placeOrder(OrderDto orderDto, String customerId);

    List<OrderDto> getOrdersByCustomer(String customerId);

    List<OrderDto> getOrdersByRestaurant(Long restaurantId);
}
