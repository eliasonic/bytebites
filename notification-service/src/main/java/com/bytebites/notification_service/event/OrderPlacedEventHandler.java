package com.bytebites.notification_service.event;

import com.bytebites.notification_service.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderPlacedEventHandler {
    private final EmailService emailService;

    @RabbitListener(queues = "${spring.rabbitmq.queue.order.placed}")
    public void handleOrderPlaced(OrderPlacedEvent event) {
        log.info("Received OrderPlacedEvent for order {} from customer {}", event.orderId(), event.customerId());
        try {
            emailService.sendOrderConfirmation(event);
            log.info("Sent confirmation email for order {}", event.orderId());
        } catch (Exception e) {
            log.error("Failed to send confirmation email for order {}", event.orderId(), e);
        }
    }
}