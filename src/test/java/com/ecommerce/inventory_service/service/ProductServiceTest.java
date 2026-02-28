package com.ecommerce.inventory_service.service;
import com.ecommerce.inventory_service.domain.Product;
import com.ecommerce.inventory_service.dto.ProductRequest;
import com.ecommerce.inventory_service.dto.ProductResponse;
import com.ecommerce.inventory_service.mapper.ProductMapper;
import com.ecommerce.inventory_service.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductRequest productRequest;
    private ProductResponse productResponse;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id("prod-001")
                .name("Laptop Stand")
                .description("Ergonomic stand")
                .price(new BigDecimal("50.00"))
                .quantity(100)
                .createdAt(Instant.now())
                .build();

        productRequest = new ProductRequest(
                "Laptop Stand",
                "Ergonomic stand",
                new BigDecimal("50.00"),
                100
        );

        productResponse = new ProductResponse(
                "prod-001",
                "Laptop Stand",
                "Ergonomic stand",
                new BigDecimal("50.00"),
                100,
                Instant.now()
        );
    }

    // ==================== CREATE ====================

    @Test
    void create_shouldSaveProductAndReturnResponse() {
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toResponse(any(Product.class))).thenReturn(productResponse);

        ProductResponse result = productService.create(productRequest);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Laptop Stand");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    // ==================== FIND ALL ====================

    @Test
    void findAll_shouldReturnListOfProducts() {
        when(productRepository.findAll()).thenReturn(List.of(product));
        when(productMapper.toResponse(product)).thenReturn(productResponse);

        List<ProductResponse> result = productService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Laptop Stand");
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoProducts() {
        when(productRepository.findAll()).thenReturn(List.of());

        List<ProductResponse> result = productService.findAll();

        assertThat(result).isEmpty();
    }

    // ==================== FIND BY ID ====================

    @Test
    void findById_shouldReturnProduct_whenExists() {
        when(productRepository.findById("prod-001")).thenReturn(Optional.of(product));
        when(productMapper.toResponse(product)).thenReturn(productResponse);

        ProductResponse result = productService.findById("prod-001");

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo("prod-001");
    }

    @Test
    void findById_shouldThrowException_whenNotFound() {
        when(productRepository.findById("invalid-id")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.findById("invalid-id"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Product not found");
    }

    // ==================== UPDATE ====================

    @Test
    void update_shouldUpdateProductAndReturnResponse() {
        when(productRepository.findById("prod-001")).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toResponse(any(Product.class))).thenReturn(productResponse);

        ProductResponse result = productService.update("prod-001", productRequest);

        assertThat(result).isNotNull();
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void update_shouldThrowException_whenProductNotFound() {
        when(productRepository.findById("invalid-id")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.update("invalid-id", productRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Product not found");
    }

    // ==================== DELETE ====================

    @Test
    void delete_shouldCallDeleteById() {
        doNothing().when(productRepository).deleteById("prod-001");

        productService.delete("prod-001");

        verify(productRepository, times(1)).deleteById("prod-001");
    }

    // ==================== DECREASE STOCK ====================

    @Test
    void decreaseStock_shouldDecreaseQuantity_whenStockSufficient() {
        when(productRepository.findById("prod-001")).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        productService.decreaseStock("prod-001", 10);

        assertThat(product.getQuantity()).isEqualTo(90);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void decreaseStock_shouldThrowException_whenStockInsufficient() {
        product.setQuantity(5);
        when(productRepository.findById("prod-001")).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> productService.decreaseStock("prod-001", 10))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Insufficient stock for product: prod-001");
    }

    @Test
    void decreaseStock_shouldThrowException_whenProductNotFound() {
        when(productRepository.findById("invalid-id")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.decreaseStock("invalid-id", 5))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Product not found: invalid-id");
    }

    // ==================== HAS STOCK ====================

    @Test
    void hasStock_shouldReturnTrue_whenStockSufficient() {
        when(productRepository.findById("prod-001")).thenReturn(Optional.of(product));

        boolean result = productService.hasStock("prod-001", 50);

        assertThat(result).isTrue();
    }

    @Test
    void hasStock_shouldReturnFalse_whenStockInsufficient() {
        when(productRepository.findById("prod-001")).thenReturn(Optional.of(product));

        boolean result = productService.hasStock("prod-001", 200);

        assertThat(result).isFalse();
    }

    @Test
    void hasStock_shouldReturnFalse_whenProductNotFound() {
        when(productRepository.findById("invalid-id")).thenReturn(Optional.empty());

        boolean result = productService.hasStock("invalid-id", 5);

        assertThat(result).isFalse();
    }
}

