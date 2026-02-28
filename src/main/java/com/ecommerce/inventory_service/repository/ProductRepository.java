package com.ecommerce.inventory_service.repository;

import com.ecommerce.inventory_service.domain.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {
}
