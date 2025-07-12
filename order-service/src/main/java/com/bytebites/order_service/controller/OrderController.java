package com.bytebites.order_service.controller;

import com.bytebites.order_service.dto.CreateOrderDto;
import com.bytebites.order_service.dto.OrderDto;
import com.bytebites.order_service.entity.OrderStatus;
import com.bytebites.order_service.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderDto> placeOrder(@RequestBody @Valid CreateOrderDto createOrderDto, @AuthenticationPrincipal String customerId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.placeOrder(createOrderDto, customerId));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderDto>> getCustomerOrders(@PathVariable String customerId) {
        return ResponseEntity.ok(orderService.getOrdersByCustomer(customerId));
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<OrderDto>> getRestaurantOrders(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(orderService.getOrdersByRestaurant(restaurantId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderDto> updateOrderStatus(@PathVariable Long id, @RequestBody OrderStatus status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
    }
}