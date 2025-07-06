package com.bytebites.restaurant_service.event;

import java.time.LocalDateTime;

public record OrderPlacedEvent(
        Long orderId,
        String customerId,
        Long restaurantId,
        Double totalAmount,
        LocalDateTime createdAt
) {}