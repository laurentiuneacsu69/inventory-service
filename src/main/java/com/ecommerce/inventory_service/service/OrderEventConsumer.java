package com.ecommerce.inventory_service.service;

import com.ecommerce.inventory_service.event.OrderPlacedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final ProductServiceImpl productService;

    @KafkaListener(topics = "order-placed", groupId = "inventory-group",
            containerFactory = "kafkaListenerContainerFactory")
    public void handleOrderPlaced(OrderPlacedEvent event) {
        log.info("Received order-placed event for product: {}", event.productId());
        productService.decreaseStock(event.productId(), event.quantity());
    }
}
