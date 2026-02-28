package com.ecommerce.inventory_service.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductResponse(
        String id,
        String name,
        String description,
        BigDecimal price,
        Integer quantity,
        Instant createdAt
) {}