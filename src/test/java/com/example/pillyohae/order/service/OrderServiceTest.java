package com.example.pillyohae.order.service;

import com.example.pillyohae.order.dto.OrderCreateRequestDto;
import com.example.pillyohae.order.dto.OrderCreateResponseDto;
import com.example.pillyohae.order.entity.Order;
import com.example.pillyohae.order.repository.OrderRepository;
import com.example.pillyohae.product.entity.Product;
import com.example.pillyohae.product.entity.type.ProductStatus;
import com.example.pillyohae.product.repository.ProductRepository;
import com.example.pillyohae.user.entity.User;
import com.example.pillyohae.user.entity.type.Role;
import com.example.pillyohae.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
class OrderServiceTest {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;

    private String email;

    @BeforeEach
    @Transactional
    void setUp() {
        email = "Test@tester.com";
        User testUser = new User("TestUser", email, "password123", "Test Address", Role.SELLER);
        Product product = new Product(testUser, "product1", "test1", "test1", "test1", 10000L, ProductStatus.SELLING);
        userRepository.save(testUser);
        productRepository.save(product);
    }

    @Test
    @Rollback(value = true)
    void createOrderByProductsWithNoCouponTest() {
        OrderCreateRequestDto.ProductOrderInfo productOrderInfo = new OrderCreateRequestDto.ProductOrderInfo(1L, 3);
        Long productId = 1L;
        List<OrderCreateRequestDto.ProductOrderInfo> productOrderInfoList = new ArrayList<>();
        productOrderInfoList.add(productOrderInfo);
        OrderCreateRequestDto orderCreateRequestDto = new OrderCreateRequestDto(productOrderInfoList, null);
        orderService.createOrderByProducts(email, orderCreateRequestDto);
        // 주문 생성
        OrderCreateResponseDto responseDto = orderService.createOrderByProducts(email, orderCreateRequestDto);

        // 주문이 성공적으로 생성되었는지 검증
        assertNotNull(responseDto.getId());
        // 주문 상세 정보 검증
        Order order = orderRepository.findById(responseDto.getId()).orElseThrow();
        assertEquals(email, order.getUser().getEmail());
        assertEquals(1, order.getOrderProducts().size());
        assertEquals(3, order.getOrderProducts().get(0).getQuantity());
    }
}