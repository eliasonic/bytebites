package com.bytebites.order_service.service;

import com.bytebites.order_service.dto.CreateOrderDto;
import com.bytebites.order_service.dto.OrderDto;
import com.bytebites.order_service.entity.OrderStatus;

import java.util.List;

public interface OrderService {
    OrderDto placeOrder(CreateOrderDto createOrderDto, String customerId);

    List<OrderDto> getOrdersByCustomer(String customerId);

    List<OrderDto> getOrdersByRestaurant(Long restaurantId);

    OrderDto getOrderById(Long orderId);

    OrderDto updateOrderStatus(Long orderId, OrderStatus status);
}
