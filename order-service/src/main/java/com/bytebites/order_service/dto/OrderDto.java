package com.bytebites.order_service.dto;

import com.bytebites.order_service.entity.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDto(
        Long id,
        String customerId,
        Long restaurantId,
        OrderStatus status,
        LocalDateTime createdAt,
        List<OrderItemDto> items,
        Double totalAmount
) {}
