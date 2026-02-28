package com.ecommerce.inventory_service.service;

import com.ecommerce.inventory_service.dto.ProductRequest;
import com.ecommerce.inventory_service.dto.ProductResponse;

import java.util.List;

public interface ProductService {

    ProductResponse create(ProductRequest request);
    List<ProductResponse> findAll();
    ProductResponse findById(String id);
    ProductResponse update(String id, ProductRequest request);
    void delete(String id);
    void decreaseStock(String productId, Integer quantity);
    boolean hasStock(String productId, Integer quantity);
}