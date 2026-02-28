package com.ecommerce.inventory_service.config;

import com.ecommerce.inventory_service.domain.Product;
import com.ecommerce.inventory_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        if (productRepository.count() > 0) {
            log.info("Products already exist, skipping initialization");
            return;
        }

        productRepository.save(Product.builder()
                .id("prod-001")
                .name("Laptop Stand")
                .description("Ergonomic aluminum laptop stand")
                .price(new BigDecimal("50.00"))
                .quantity(100)
                .createdAt(Instant.now())
                .build());

        productRepository.save(Product.builder()
                .id("prod-002")
                .name("Mechanical Keyboard")
                .description("TKL mechanical keyboard with RGB")
                .price(new BigDecimal("100.00"))
                .quantity(50)
                .createdAt(Instant.now())
                .build());

        productRepository.save(Product.builder()
                .id("prod-003")
                .name("Gaming Mouse")
                .description("Wireless gaming mouse 25600 DPI")
                .price(new BigDecimal("99.99"))
                .quantity(75)
                .createdAt(Instant.now())
                .build());

        productRepository.save(Product.builder()
                .id("prod-004")
                .name("USB Hub")
                .description("7-port USB 3.0 hub with power delivery")
                .price(new BigDecimal("89.50"))
                .quantity(200)
                .createdAt(Instant.now())
                .build());

        productRepository.save(Product.builder()
                .id("prod-005")
                .name("Monitor Light Bar")
                .description("LED monitor light bar with wireless control")
                .price(new BigDecimal("45.00"))
                .quantity(150)
                .createdAt(Instant.now())
                .build());

        log.info("Sample products inserted successfully!");
    }
}
