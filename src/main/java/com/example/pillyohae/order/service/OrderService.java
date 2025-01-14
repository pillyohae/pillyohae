package com.example.pillyohae.order.service;

import com.example.pillyohae.cart.entity.Cart;
import com.example.pillyohae.cart.repository.CartRepository;
import com.example.pillyohae.coupon.entity.CouponTemplate;
import com.example.pillyohae.coupon.entity.IssuedCoupon;
import com.example.pillyohae.coupon.repository.IssuedCouponRepository;
import com.example.pillyohae.order.dto.*;
import com.example.pillyohae.order.entity.Order;
import com.example.pillyohae.order.entity.OrderProduct;
import com.example.pillyohae.order.entity.status.OrderItemStatus;
import com.example.pillyohae.order.entity.status.OrderStatus;
import com.example.pillyohae.order.repository.OrderProductRepository;
import com.example.pillyohae.order.repository.OrderRepository;
import com.example.pillyohae.product.entity.Product;
import com.example.pillyohae.product.repository.ProductRepository;
import com.example.pillyohae.user.entity.User;
import com.example.pillyohae.user.repository.UserRepository;
import com.example.pillyohae.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    private final OrderProductRepository orderProductRepository;
    private final IssuedCouponRepository issuedCouponRepository;


    // 상품 주문 생성
    @Transactional
    public OrderCreateResponseDto createOrderByProducts(String email, OrderCreateRequestDto requestDto) {
        User user = userService.findByEmail(email);
        List<Long> productIds = requestDto.getProductInfos().stream().map(OrderCreateRequestDto.ProductOrderInfo::getProductId).toList();
        // 저장에 필요한 product를 한번에 불러옴
        List<Product> purchaseProducts = productRepository.findByProductIdIn(productIds);
        if(purchaseProducts.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"there is no product to order");
        }
        Order order = new Order(user);
        List<OrderProduct> orderProducts = new ArrayList<>();
        // 주문 상품들을 orderProduct 로 저장
        for(OrderCreateRequestDto.ProductOrderInfo productOrderInfo : requestDto.getProductInfos()) {
            Product product = purchaseProducts.stream()
                    .filter(p -> p.getProductId().equals(productOrderInfo.getProductId()))
                    .findFirst()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,"product not found"));
            orderProducts.add(new OrderProduct(productOrderInfo.getQuantity(), product.getPrice(), product.getProductId(), order));
        }
        Order savedOrder = orderRepository.save(order);
        orderProductRepository.saveAll(orderProducts);

        // 쿠폰 적용 일단 한개만 적용하도록 설정
        if(requestDto.getCouponIds() != null ){
            issuedCouponRepository.findById(requestDto.getCouponIds().get(0)).ifPresent(order::applyCoupon);
        }
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

    // 결제가 모두 완료된 order 단건 조회
    @Transactional
    public BuyerOrderDetailInfo getOrderDetailAfterPayment(String email, UUID orderId) {
        User user = userService.findByEmail(email);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        if(OrderStatus.PENDING.equals(order.getStatus())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Order is in pending");
        }
        if (!order.getUser().equals(user)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Order is not owned by user");
        }
        // orderItem에 저장된 정보로 조회
        List<BuyerOrderDetailInfo.BuyerOrderProductInfo> itemInfos = orderRepository.findBuyerOrderDetailAfterPayment(orderId);
        return new BuyerOrderDetailInfo(itemInfos);
    }

    // 결제가 완료되기 전 order 단건조회
    @Transactional
    public BuyerOrderDetailInfo getOrderDetailBeforePayment(String email, UUID orderId) {
        User user = userService.findByEmail(email);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        if(!OrderStatus.PENDING.equals(order.getStatus())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Order is not in pending");
        }
        if (!order.getUser().equals(user)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Order is not owned by user");
        }
        List<BuyerOrderDetailInfo.BuyerOrderProductInfo> itemInfos = orderRepository.findBuyerOrderDetailBeforePayment(orderId);
        return new BuyerOrderDetailInfo(itemInfos);
    }

    // seller orderItem 상태 수정
    @Transactional
    public SellerOrderItemStatusChangeResponseDto changeOrderItemStatus(String email, Long orderItemId, OrderItemStatus newStatus) {
        User seller = userService.findByEmail(email);
        OrderProduct orderProduct = orderProductRepository.findById(orderItemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order item not found"));
        if (!seller.equals(orderProduct.getSeller())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Order item is not owned by user");
        }
        orderProduct.updateStatus(newStatus);

        return new SellerOrderItemStatusChangeResponseDto(orderProduct.getId(), orderProduct.getStatus().getValue());
    }

    //쿠폰 사용
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public OrderUseCouponResponseDto useCoupon(String email, UUID orderId, Long couponId) {
        if (email == null || orderId == null || couponId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Required parameters cannot be null");
        }
        User user = userService.findByEmail(email);
        IssuedCoupon coupon = issuedCouponRepository.findById(couponId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Coupon not found"));
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        validateCouponToUse(coupon, user);
        // 최소 금액보다 낮을경우 예외
        if (coupon.getCouponTemplate().getMinimumPrice() >= order.getTotalPrice()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Coupon could not be used for this order");
        }
        order.applyCoupon(coupon);
        return new OrderUseCouponResponseDto(orderId, couponId, order.getDiscountAmount());
    }


    private Double calculateOrderItemPrice(Double price, Integer quantity) {
        return price * quantity;
    }



    public String makeOrderName(String firstProductName, Integer firstProductQuantity, Integer productKinds ){
        if (productKinds == 1){
            return firstProductName + " " + firstProductQuantity + " 개";
        }
        return firstProductName + " " + firstProductQuantity + "개" + " 외 " + (productKinds - 1) + " 건" ;
    }

    // 쿠폰이 만료되거나 사용될 경우 또는 쿠폰 사용을 금지했을경우 예외
    private void validateCouponToUse(IssuedCoupon coupon, User user) {
        if(CouponTemplate.CouponStatus.INACTIVE.equals(coupon.getCouponTemplate().getStatus())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Coupon is not active");
        }
        if (!coupon.getUser().equals(user)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Coupon is not owned by user");
        }
        if (LocalDateTime.now().isAfter(coupon.getExpiredAt())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Coupon is expired");
        }
        if (IssuedCoupon.CouponStatus.USED.equals(coupon.getStatus())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Coupon is used");
        }

    }
}
