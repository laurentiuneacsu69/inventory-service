package com.ecommerce.inventory_service.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Document(collection = "products")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    private String id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer quantity;
    private Instant createdAt;

    public static Product create(String name, String description, BigDecimal price, Integer quantity) {
        return Product.builder()
                .id(UUID.randomUUID().toString())
                .name(name)
                .description(description)
                .price(price)
                .quantity(quantity)
                .createdAt(Instant.now())
                .build();
    }
}
