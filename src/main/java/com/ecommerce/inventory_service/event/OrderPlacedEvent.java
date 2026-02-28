package com.ecommerce.inventory_service.event;

public record OrderPlacedEvent(
        String productId,
        Integer quantity
) {}