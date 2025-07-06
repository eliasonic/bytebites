package com.bytebites.order_service.controller;

import com.bytebites.order_service.dto.OrderDto;
import com.bytebites.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public OrderDto placeOrder(@RequestBody OrderDto orderDto, @AuthenticationPrincipal String customerId) {
        return orderService.placeOrder(orderDto, customerId);
    }

    @GetMapping("/customer/{customerId}")
    public List<OrderDto> getCustomerOrders(@PathVariable String customerId) {
        return orderService.getOrdersByCustomer(customerId);
    }

    @GetMapping("/restaurant/{restaurantId}")
    public List<OrderDto> getRestaurantOrders(@PathVariable Long restaurantId) {
        return orderService.getOrdersByRestaurant(restaurantId);
    }
}