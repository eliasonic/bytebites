package com.bytebites.order_service.dto;

import jakarta.validation.constraints.*;

public record OrderItemDto(
        @NotNull(message = "Menu item ID is required")
        Long menuItemId,

        @NotBlank(message = "Menu item name is required")
        String menuItemName,

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        @Max(value = 100, message = "Quantity cannot exceed 100")
        Integer quantity,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.01", message = "Price must be at least 0.01")
        Double price
) {}