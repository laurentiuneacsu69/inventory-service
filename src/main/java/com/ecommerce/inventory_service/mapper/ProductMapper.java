package com.ecommerce.inventory_service.mapper;

import com.ecommerce.inventory_service.domain.Product;
import com.ecommerce.inventory_service.dto.ProductResponse;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),
                product.getCreatedAt()
        );
    }
}