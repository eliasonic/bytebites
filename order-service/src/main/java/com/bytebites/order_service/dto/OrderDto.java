package com.bytebites.order_service.dto;

import com.bytebites.order_service.entity.OrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.List;

public record OrderDto(
        Long id,

        @NotNull(message = "Customer ID is required")
        String customerId,

        @NotNull(message = "Restaurant ID is required")
        Long restaurantId,

        @NotNull(message = "Order status is required")
        OrderStatus status,

        LocalDateTime createdAt,

        @NotEmpty(message = "Order must contain at least one item")
        @Valid
        List<OrderItemDto> items,

        @NotNull(message = "Total amount is required")
        @Positive(message = "Total amount must be greater than zero")
        Double totalAmount
) {}
