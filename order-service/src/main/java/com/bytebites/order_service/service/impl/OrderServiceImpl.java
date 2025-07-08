package com.bytebites.order_service.service.impl;

import com.bytebites.order_service.config.RabbitMQConfig;
import com.bytebites.order_service.dto.OrderDto;
import com.bytebites.order_service.dto.OrderItemDto;
import com.bytebites.order_service.entity.Order;
import com.bytebites.order_service.entity.OrderItem;
import com.bytebites.order_service.entity.OrderStatus;
import com.bytebites.order_service.event.OrderPlacedEvent;
import com.bytebites.order_service.mapper.OrderItemMapper;
import com.bytebites.order_service.mapper.OrderMapper;
import com.bytebites.order_service.repository.OrderItemRepository;
import com.bytebites.order_service.repository.OrderRepository;
import com.bytebites.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    @PreAuthorize("hasRole('CUSTOMER')")
    public OrderDto placeOrder(OrderDto orderDto, String customerId) {
        Order order = orderMapper.toEntity(orderDto);
        order.setCustomerId(customerId);
        order.setStatus(OrderStatus.PENDING);

        double totalAmount = orderDto.items().stream()
                .mapToDouble(item -> item.price() * item.quantity())
                .sum();
        order.setTotalAmount(totalAmount);

        order.getItems().forEach(item -> item.setOrder(order));
        Order savedOrder = orderRepository.save(order);

        publishOrderPlacedEvent(savedOrder);
        return orderMapper.toDto(savedOrder);
    }

    @PreAuthorize("#customerId == authentication.principal or hasRole('RESTAURANT_OWNER')")
    public List<OrderDto> getOrdersByCustomer(String customerId) {
        return orderRepository.findByCustomerId(customerId)
                .stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public List<OrderDto> getOrdersByRestaurant(Long restaurantId) {
        return orderRepository.findByRestaurantId(restaurantId)
                .stream()
                .map(orderMapper::toDto)
                .toList();
    }

    private void publishOrderPlacedEvent(Order order) {
        OrderPlacedEvent event = new OrderPlacedEvent(
                order.getId(),
                order.getCustomerId(),
                order.getRestaurantId(),
                order.getTotalAmount(),
                order.getCreatedAt()
        );
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY,
                event
        );
    }
}