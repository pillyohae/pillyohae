package com.example.pillyohae.domain.order.service;

import com.example.pillyohae.domain.order.dto.OrderCreateByProductRequestDto;
import com.example.pillyohae.domain.order.dto.OrderCreateResponseDto;
import com.example.pillyohae.domain.order.entity.Order;
import com.example.pillyohae.domain.order.entity.OrderItem;
import com.example.pillyohae.domain.order.repository.OrderRepository;
import com.example.pillyohae.product.entity.Product;
import com.example.pillyohae.product.repository.ProductRepository;
import com.example.pillyohae.user.entity.User;
import com.example.pillyohae.user.repository.UserRepository;
import com.example.pillyohae.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final ProductRepository productRepository;
    // cart 정보를 주문 정보로 변환 후 저장
//    public OrderCreateResponseDto createOrderByCart(Long userId, Long cartId){
//        // cart 가져옴
//
//    }



    // 상품 단건 구매
    @Transactional
    public OrderCreateResponseDto createOrderByProduct(String email, OrderCreateByProductRequestDto requestDto) {
        User user = userService.findByEmail(email);
        Product product = productRepository.findById(requestDto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        // 재고 처리 및 품절 로직 필요
        Order order = new Order(user);
        OrderItem orderItem = new OrderItem(product.getProductName()
                ,calculateOrderItemPrice(Double.valueOf(product.getPrice()), requestDto.getQuantity())
                ,requestDto.getQuantity(),requestDto.getProductId(), order);
        order.updateTotalPrice();

        return new OrderCreateResponseDto(order.getId());
    }

    // order 조회

    // order 내역 조회

    // order 수정

    // order Item 조회

    private Double calculateOrderItemPrice(Double price, Long quantity) {
        return price * quantity;
    }
}
