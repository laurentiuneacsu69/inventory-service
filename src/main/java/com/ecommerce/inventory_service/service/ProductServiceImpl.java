package com.ecommerce.inventory_service.service;

import com.ecommerce.inventory_service.domain.Product;
import com.ecommerce.inventory_service.dto.ProductRequest;
import com.ecommerce.inventory_service.dto.ProductResponse;
import com.ecommerce.inventory_service.mapper.ProductMapper;
import com.ecommerce.inventory_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    @Override
    public ProductResponse create(ProductRequest request) {
        Product product = Product.create(
                request.name(),
                request.description(),
                request.price(),
                request.quantity()
        );

        productRepository.save(product);

        return productMapper.toResponse(product);
    }

    @Override
    public List<ProductResponse> findAll() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }

    @Override
    public ProductResponse findById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return productMapper.toResponse(product);
    }

    @Override
    public ProductResponse update(String id, ProductRequest request) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setQuantity(request.quantity());

        productRepository.save(product);

        return productMapper.toResponse(product);
    }

    @Override
    public void delete(String id) {
        productRepository.deleteById(id);
    }

    @Override
    public void decreaseStock(String productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

        if (product.getQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock for product: " + productId);
        }

        product.setQuantity(product.getQuantity() - quantity);
        productRepository.save(product);
    }

    @Override
    public boolean hasStock(String productId, Integer quantity) {
        return productRepository.findById(productId)
                .map(product -> product.getQuantity() >= quantity)
                .orElse(false);
    }
}