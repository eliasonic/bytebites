package com.bytebites.order_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateOrderDto(
        @NotNull(message = "Restaurant ID is required")
        Long restaurantId,

        @NotEmpty(message = "Order must contain at least one item")
        @Valid
        List<OrderItemDto> items
) {}
