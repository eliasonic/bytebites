package com.bytebites.restaurant_service.event;

import com.bytebites.restaurant_service.config.RabbitMQConfig;
import com.bytebites.restaurant_service.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderPlacedEventHandler {
    private final RestaurantService restaurantService;

    @RabbitListener(queues = "${spring.rabbitmq.queue.order.placed}")
    public void handleOrderPlaced(OrderPlacedEvent event) {
        log.info("Received OrderPlacedEvent for order {} at restaurant {}", event.orderId(), event.restaurantId());

        restaurantService.startOrderPreparation(event);
    }
}