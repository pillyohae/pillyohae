package com.example.pillyohae.order.service;

import com.example.pillyohae.cart.entity.Cart;
import com.example.pillyohae.cart.repository.CartRepository;
import com.example.pillyohae.order.dto.*;
import com.example.pillyohae.order.entity.Order;
import com.example.pillyohae.order.entity.OrderItem;
import com.example.pillyohae.order.entity.status.OrderItemStatus;
import com.example.pillyohae.order.repository.OrderItemRepository;
import com.example.pillyohae.order.repository.OrderRepository;
import com.example.pillyohae.product.entity.Product;
import com.example.pillyohae.product.repository.ProductRepository;
import com.example.pillyohae.user.entity.User;
import com.example.pillyohae.user.repository.UserRepository;
import com.example.pillyohae.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    public OrderCreateResponseDto createOrderByCart(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Order order = convertCartToOrder(user);
        return new OrderCreateResponseDto(order.getId());

    }


    // 상품 단건 구매
    @Transactional
    public OrderCreateResponseDto createOrderByProduct(String email, OrderCreateByProductRequestDto requestDto) {
        User user = userService.findByEmail(email);
        Product product = productRepository.findById(requestDto.getProductId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        // 재고 처리 및 품절 로직 필요
        String orderName = product.getProductName() + " " + requestDto.getQuantity() + " 개";
        Order order = new Order(orderName, user);
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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date must be before end date");
        }
        if (pageNumber < 0 || pageSize <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid pagination parameters");
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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        if(!order.getUser().equals(user)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Order is not owned by user");
        }
        List<BuyerOrderDetailInfo.BuyerOrderItemInfo> itemInfos = orderRepository.findBuyerOrderDetail(orderId);
        return new BuyerOrderDetailInfo(itemInfos);
    }

    // seller orderItem 상태 수정
    @Transactional
    public SellerOrderItemStatusChangeResponseDto changeOrderItemStatus(String email, Long orderItemId, OrderItemStatus newStatus) {
        User seller = userService.findByEmail(email);
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order item not found"));
        if (!seller.equals(orderItem.getSeller())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Order item is not owned by user");
        }
        orderItem.updateStatus(newStatus);

        return new SellerOrderItemStatusChangeResponseDto(orderItem.getId(), orderItem.getStatus().getValue());
    }


    private Double calculateOrderItemPrice(Double price, Long quantity) {
        return price * quantity;
    }

    @Transactional
    protected Order convertCartToOrder(User user) {
        // fetch join으로 product도 같이 갖고옴
        List<Cart> carts = cartRepository.findCartsWithProductsByUserId(user.getId());

        if (carts.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty");
        }

        // Order 생성 및 저장
        String orderName = carts.get(0).getProduct().getProductName() + " " + carts.get(0).getQuantity() + "개" + " 외 " + (carts.size() - 1) + " 건";
        Order order = new Order(orderName, user);
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
        order.updateTotalPrice();
        return order;
    }
}
