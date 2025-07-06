package com.bytebites.order_service.dto;

public record OrderItemDto(
        Long menuItemId,
        String menuItemName,
        Integer quantity,
        Double price
) {}