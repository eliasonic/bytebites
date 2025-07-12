package com.bytebites.order_service.controller;

import com.bytebites.order_service.dto.CreateOrderDto;
import com.bytebites.order_service.dto.OrderDto;
import com.bytebites.order_service.dto.OrderItemDto;
import com.bytebites.order_service.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest()
@Testcontainers
@AutoConfigureMockMvc
@Transactional
class OrderControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @Container
    static RabbitMQContainer rabbit = new RabbitMQContainer("rabbitmq:3-management-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${jwt.secret}")
    private String jwtSecret;
    private String customerToken;
    private String restaurantOwnerToken;
    private String adminToken;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");

        registry.add("jwt.secret", () -> "mySecretKeyForTestingPurposesOnlyAndShouldBeLongEnough");
        registry.add("jwt.expiration", () -> "1800000");

        registry.add("spring.rabbitmq.host", () -> "localhost");
        registry.add("spring.rabbitmq.port", () -> "5672");
        registry.add("spring.rabbitmq.username", () -> "guest");
        registry.add("spring.rabbitmq.password", () -> "guest");
        registry.add("spring.rabbitmq.queue.exchange", () -> "order.events");
        registry.add("spring.rabbitmq.queue.bindingKey", () -> "order.placed");
    }

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();

        customerToken = generateJwtToken("customer123", List.of("ROLE_CUSTOMER"));
        restaurantOwnerToken = generateJwtToken("restaurant_owner", List.of("ROLE_RESTAURANT_OWNER"));
        adminToken = generateJwtToken("admin", List.of("ROLE_ADMIN"));
    }

    private String generateJwtToken(String username, List<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);
        claims.put("sub", username);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1800000))
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    @Test
    void shouldHavePostgreSQLContainerRunning() {
        assertTrue(postgres.isRunning());
        assertTrue(postgres.isCreated());
    }

    @Test
    void shouldHaveRabbitMQContainerRunning() {
        assertTrue(rabbit.isRunning());
        assertTrue(rabbit.isCreated());
    }

    @Test
    void shouldPlaceOrderSuccessfullyWithJwtToken() throws Exception {
        CreateOrderDto createOrderDto = new CreateOrderDto(
                1L,
                List.of(
                        new OrderItemDto(101L, "Pizza Margherita", 2, 12.99),
                        new OrderItemDto(102L, "Coca Cola", 1, 2.50)
                )
        );

        mockMvc.perform(post("/api/v1/orders")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createOrderDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerId").value("customer123"))
                .andExpect(jsonPath("$.restaurantId").value(1))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.totalAmount").value(28.48))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items.length()").value(2));
    }

    @Test
    void shouldRejectRequestWithoutToken() throws Exception {
        CreateOrderDto createOrderDto = new CreateOrderDto(
                1L,
                List.of(new OrderItemDto(101L, "Pizza", 1, 10.00))
        );

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createOrderDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectRequestWithInvalidToken() throws Exception {
        CreateOrderDto createOrderDto = new CreateOrderDto(
                1L,
                List.of(new OrderItemDto(101L, "Pizza", 1, 10.00))
        );

        mockMvc.perform(post("/api/v1/orders")
                        .header("Authorization", "Bearer invalid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createOrderDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldHandleEmptyOrderItemsListWithJwtToken() throws Exception {
        CreateOrderDto createOrderDto = new CreateOrderDto(
                1L,
                List.of()
        );

        mockMvc.perform(post("/api/v1/orders")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createOrderDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetOrdersByCustomerWithJwtToken() throws Exception {
        CreateOrderDto order = new CreateOrderDto(
                1L,
                List.of(new OrderItemDto(101L, "Pizza", 1, 10.00))
        );

        mockMvc.perform(post("/api/v1/orders")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/orders/customer/customer123")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].customerId").value("customer123"));
    }

    @Test
    void shouldGetOrdersByRestaurantWithJwtToken() throws Exception {
        Long restaurantId = 1L;
        CreateOrderDto order = new CreateOrderDto(
                restaurantId,
                List.of(new OrderItemDto(101L, "Pizza", 1, 10.00))
        );

        mockMvc.perform(post("/api/v1/orders")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/orders/restaurant/" + restaurantId)
                        .header("Authorization", "Bearer " + restaurantOwnerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].restaurantId").value(restaurantId.intValue()));
    }

    @Test
    void shouldThrowExceptionWhenOrderNotFoundWithAdminToken() throws Exception {
        Long nonExistentOrderId = 999L;

        mockMvc.perform(get("/api/v1/orders/" + nonExistentOrderId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateOrderStatusWithAdminToken() throws Exception {
        CreateOrderDto createOrderDto = new CreateOrderDto(
                1L,
                List.of(new OrderItemDto(101L, "Pizza", 1, 10.00))
        );

        String response = mockMvc.perform(post("/api/v1/orders")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createOrderDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        OrderDto createdOrder = objectMapper.readValue(response, OrderDto.class);

        mockMvc.perform(put("/api/v1/orders/" + createdOrder.id() + "/status")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"CONFIRMED\""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }
}