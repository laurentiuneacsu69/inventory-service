package com.ecommerce.inventory_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ProductRequest(

        @NotNull
        String name,
        String description,
        @NotNull
        BigDecimal price,

        @NotNull
        @Min(1)
        Integer quantity
) {}