package com.ecommerce.inventory_service.controller;

import com.ecommerce.inventory_service.dto.ProductRequest;
import com.ecommerce.inventory_service.dto.ProductResponse;
import com.ecommerce.inventory_service.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse create(@RequestBody @Valid ProductRequest request) {
        return productService.create(request);
    }

    @GetMapping
    public List<ProductResponse> findAll() {
        return productService.findAll();
    }

    @GetMapping("/{id}")
    public ProductResponse findById(@PathVariable String id) {
        return productService.findById(id);
    }

    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable String id,
                                  @RequestBody @Valid ProductRequest request) {
        return productService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        productService.delete(id);
    }

    @GetMapping("/{id}/stock-check")
    public boolean checkStock(@PathVariable String id,
                              @RequestParam Integer quantity) {
        return productService.hasStock(id, quantity);
    }
}