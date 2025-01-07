package com.example.pillyohae.domain.order.service;

import com.example.pillyohae.cart.entity.Cart;
import com.example.pillyohae.cart.repository.CartRepository;
import com.example.pillyohae.domain.order.dto.*;
import com.example.pillyohae.domain.order.entity.Order;
import com.example.pillyohae.domain.order.entity.OrderItem;
import com.example.pillyohae.domain.order.entity.status.OrderItemStatus;
import com.example.pillyohae.domain.order.repository.OrderItemRepository;
import com.example.pillyohae.domain.order.repository.OrderRepository;
import com.example.pillyohae.product.entity.Product;
import com.example.pillyohae.product.repository.ProductRepository;
import com.example.pillyohae.user.entity.User;
import com.example.pillyohae.user.repository.UserRepository;
import com.example.pillyohae.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final OrderItemRepository orderItemRepository;

    //cart 정보를 주문 정보로 변환 후 저장
    @Transactional
    public OrderCreateResponseDto createOrderByCart(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Order order = convertCartToOrder(user);
        return new OrderCreateResponseDto(order.getId());

    }


    // 상품 단건 구매
    @Transactional
    public OrderCreateResponseDto createOrderByProduct(String email, OrderCreateByProductRequestDto requestDto) {
        User user = userService.findByEmail(email);
        Product product = productRepository.findById(requestDto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        // 재고 처리 및 품절 로직 필요
        Order order = new Order(user);
        OrderItem orderItem = new OrderItem(product.getProductName()
                , calculateOrderItemPrice(Double.valueOf(product.getPrice()), requestDto.getQuantity())
                , requestDto.getQuantity(), requestDto.getProductId(), order);
        order.updateTotalPrice();
        Order savedOrder = orderRepository.save(order);
        orderItemRepository.save(orderItem);
        return new OrderCreateResponseDto(savedOrder.getId());
    }

    // buyer의 order 내역 조회
    @Transactional
    public BuyerOrderSearchResponseDto findOrder(String email, LocalDateTime startAt, LocalDateTime endAt, Long pageNumber, Long pageSize) {
        User user = userService.findByEmail(email);
        if (startAt.isAfter(endAt)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
        if (pageNumber < 0 || pageSize <= 0) {
            throw new IllegalArgumentException("Invalid pagination parameters");
        }
        List<BuyerOrderInfo> orderInfoList = orderRepository.findBuyerOrders(user.getId(), startAt, endAt, pageNumber, pageSize);
        BuyerOrderSearchResponseDto.PageInfo pageInfo = new BuyerOrderSearchResponseDto.PageInfo(pageNumber, pageSize);
        return new BuyerOrderSearchResponseDto(orderInfoList, pageInfo);

    }
    // order 단건 조회
    @Transactional
    public BuyerOrderDetailInfo getOrderDetail(String email, UUID orderId) {
        User user = userService.findByEmail(email);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if(order.getUser() != user){
            throw new IllegalArgumentException("Order is not owned by user");
        }
        List<BuyerOrderDetailInfo.BuyerOrderItemInfo> itemInfos = orderRepository.findBuyerOrderDetail(orderId);
        return new BuyerOrderDetailInfo(itemInfos);
    }

    // seller orderItem 상태 수정
    @Transactional
    public SellerOrderItemStatusChangeResponseDto changeOrderItemStatus(String email, Long orderItemId, OrderItemStatus newStatus) {
        User seller = userService.findByEmail(email);
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new RuntimeException("Order item not found"));
        if(!seller.equals(orderItem.getSeller()) ){
            throw new IllegalArgumentException("Order item is not owned by user");
        }
        orderItem.updateStatus(newStatus);

        return new SellerOrderItemStatusChangeResponseDto(orderItem.getId(),orderItem.getStatus().getValue());
    }


    private Double calculateOrderItemPrice(Double price, Long quantity) {
        return price * quantity;
    }

    @Transactional
    protected Order convertCartToOrder(User user) {
        // fetch join으로 product도 같이 갖고옴
        List<Cart> carts = cartRepository.findCartsWithProductsByUserId(user.getId());
        // Order 생성 및 저장
        Order order = new Order(user);
        orderRepository.save(order);

        // OrderItem 생성
        List<OrderItem> orderItems = new ArrayList<>();
        for (Cart cart : carts) {
            Product product = cart.getProduct();
            OrderItem orderItem = new OrderItem(
                    product.getProductName(),
                    Double.valueOf(product.getPrice()),
                    Long.valueOf(cart.getQuantity()),
                    product.getProductId(),
                    order
            );
            orderItems.add(orderItem);
        }

        // OrderItem 저장
        orderItemRepository.saveAll(orderItems);

        return order;
    }
}
