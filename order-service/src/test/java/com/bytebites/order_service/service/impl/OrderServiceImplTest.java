package com.bytebites.order_service.service.impl;

import com.bytebites.order_service.config.RabbitMQConfig;
import com.bytebites.order_service.dto.CreateOrderDto;
import com.bytebites.order_service.dto.OrderDto;
import com.bytebites.order_service.dto.OrderItemDto;
import com.bytebites.order_service.entity.Order;
import com.bytebites.order_service.entity.OrderItem;
import com.bytebites.order_service.entity.OrderStatus;
import com.bytebites.order_service.event.OrderPlacedEvent;
import com.bytebites.order_service.mapper.OrderMapper;
import com.bytebites.order_service.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private RabbitMQConfig rabbitMQConfig;

    @InjectMocks
    private OrderServiceImpl orderService;

    private CreateOrderDto createOrderDto;
    private Order order;
    private OrderDto orderDto;

    @BeforeEach
    void setUp() {
        OrderItemDto orderItemDto = new OrderItemDto(1L, "Pizza", 1, 15.50);
        createOrderDto = new CreateOrderDto(1L, List.of(orderItemDto));

        order = new Order();
        order.setId(1L);
        order.setCustomerId("customer-123");
        order.setRestaurantId(1L);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(15.50);
        order.setCreatedAt(LocalDateTime.now());
        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setMenuItemId(1L);
        orderItem.setMenuItemName("Pizza");
        orderItem.setQuantity(1);
        orderItem.setPrice(15.50);
        order.setItems(List.of(orderItem));

        orderDto = new OrderDto(1L, "customer-123", 1L, OrderStatus.PENDING, LocalDateTime.now(), List.of(orderItemDto), 15.50);
    }

    @Test
    @DisplayName("placeOrder - Success")
    void placeOrder_shouldSucceed() {
        String customerId = "customer-123";
        when(orderMapper.toEntity(createOrderDto)).thenReturn(order);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.toDto(order)).thenReturn(orderDto);
        when(rabbitMQConfig.getExchange()).thenReturn("order-exchange");
        when(rabbitMQConfig.getBindingKey()).thenReturn("order.placed");

        OrderDto result = orderService.placeOrder(createOrderDto, customerId);

        assertNotNull(result);
        assertEquals(orderDto, result);

        ArgumentCaptor<Order> orderArgumentCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderArgumentCaptor.capture());
        Order savedOrder = orderArgumentCaptor.getValue();

        assertEquals(customerId, savedOrder.getCustomerId());
        assertEquals(OrderStatus.PENDING, savedOrder.getStatus());
        assertEquals(15.50, savedOrder.getTotalAmount());
        assertNotNull(savedOrder.getItems());
        assertFalse(savedOrder.getItems().isEmpty());
        assertEquals(order, savedOrder.getItems().get(0).getOrder());

        ArgumentCaptor<OrderPlacedEvent> eventCaptor = ArgumentCaptor.forClass(OrderPlacedEvent.class);
        verify(rabbitTemplate).convertAndSend(eq("order-exchange"), eq("order.placed"), eventCaptor.capture());
        OrderPlacedEvent capturedEvent = eventCaptor.getValue();

        assertEquals(order.getId(), capturedEvent.orderId());
        assertEquals(order.getCustomerId(), capturedEvent.customerId());
    }

    @Test
    @DisplayName("placeOrder - Failure due to repository error")
    void placeOrder_shouldFailWhenRepositoryThrowsException() {
        String customerId = "customer-123";
        when(orderMapper.toEntity(createOrderDto)).thenReturn(order);
        when(orderRepository.save(any(Order.class))).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> orderService.placeOrder(createOrderDto, customerId));
        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(OrderPlacedEvent.class));
    }


    @Test
    @DisplayName("getOrdersByCustomer - Success")
    void getOrdersByCustomer_shouldReturnOrders() {
        String customerId = "customer-123";
        when(orderRepository.findByCustomerId(customerId)).thenReturn(List.of(order));
        when(orderMapper.toDto(order)).thenReturn(orderDto);

        List<OrderDto> result = orderService.getOrdersByCustomer(customerId);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(orderDto, result.get(0));
        verify(orderRepository).findByCustomerId(customerId);
    }

    @Test
    @DisplayName("getOrdersByCustomer - No Orders Found")
    void getOrdersByCustomer_shouldReturnEmptyListWhenNoOrders() {
        String customerId = "customer-nonexistent";
        when(orderRepository.findByCustomerId(customerId)).thenReturn(Collections.emptyList());

        List<OrderDto> result = orderService.getOrdersByCustomer(customerId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(orderRepository).findByCustomerId(customerId);
    }

    @Test
    @DisplayName("getOrdersByRestaurant - Success")
    void getOrdersByRestaurant_shouldReturnOrders() {
        Long restaurantId = 1L;
        when(orderRepository.findByRestaurantId(restaurantId)).thenReturn(List.of(order));
        when(orderMapper.toDto(order)).thenReturn(orderDto);

        List<OrderDto> result = orderService.getOrdersByRestaurant(restaurantId);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(orderDto, result.get(0));
        verify(orderRepository).findByRestaurantId(restaurantId);
    }

    @Test
    @DisplayName("getOrdersByRestaurant - No Orders Found")
    void getOrdersByRestaurant_shouldReturnEmptyListWhenNoOrders() {
        Long restaurantId = 99L;
        when(orderRepository.findByRestaurantId(restaurantId)).thenReturn(Collections.emptyList());

        List<OrderDto> result = orderService.getOrdersByRestaurant(restaurantId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(orderRepository).findByRestaurantId(restaurantId);
    }
}