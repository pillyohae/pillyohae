package com.example.pillyohae.order.service;

import com.example.pillyohae.cart.repository.CartRepository;
import com.example.pillyohae.coupon.entity.CouponTemplate;
import com.example.pillyohae.coupon.entity.IssuedCoupon;
import com.example.pillyohae.coupon.repository.IssuedCouponRepository;
import com.example.pillyohae.global.entity.address.ShippingAddress;
import com.example.pillyohae.order.dto.*;
import com.example.pillyohae.order.entity.Order;
import com.example.pillyohae.order.entity.OrderProduct;
import com.example.pillyohae.order.entity.status.OrderProductStatus;
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
        List<Product> purchaseProducts = fetchProducts(requestDto.getProductInfos());
        validateProducts(purchaseProducts);

        Order order = createOrder(user);
        List<OrderProduct> orderProducts = createOrderProducts(purchaseProducts, requestDto.getProductInfos(), order);

        Order savedOrder = saveOrderAndProducts(order, orderProducts);
        applyCouponIfPresent(savedOrder, requestDto.getCouponIds());

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
    public SellerOrderItemStatusChangeResponseDto changeOrderItemStatus(String email, Long orderItemId, OrderProductStatus newStatus) {
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


    private List<Product> fetchProducts(List<OrderCreateRequestDto.ProductOrderInfo> productInfos) {
        List<Long> productIds = productInfos.stream()
                .map(OrderCreateRequestDto.ProductOrderInfo::getProductId)
                .toList();
        return productRepository.findByProductIdIn(productIds);
    }

    private void validateProducts(List<Product> products) {
        if (products.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "There is no product to order");
        }
    }

    private Order createOrder(User user) {
        // TODO: Replace dummy address with actual user address in the future
        ShippingAddress shippingAddress = createShippingAddress();
        return new Order(user, shippingAddress);
    }

    private ShippingAddress createShippingAddress() {
        // TODO: This should be replaced with actual user address data
        return new ShippingAddress("TEST", "010-0000-0000", "TEST", "TEST", "TEST");
    }

    private List<OrderProduct> createOrderProducts(
            List<Product> purchaseProducts,
            List<OrderCreateRequestDto.ProductOrderInfo> productInfos,
            Order order
    ) {
        return productInfos.stream()
                .map(productInfo -> createOrderProduct(purchaseProducts, productInfo, order))
                .collect(Collectors.toList());
    }

    private OrderProduct createOrderProduct(
            List<Product> purchaseProducts,
            OrderCreateRequestDto.ProductOrderInfo productInfo,
            Order order
    ) {
        Product product = findProductById(purchaseProducts, productInfo.getProductId());
        return new OrderProduct(
                productInfo.getQuantity(),
                product.getPrice(),
                product.getProductId(),
                product.getUser(),
                order
        );
    }

    private Product findProductById(List<Product> products, Long productId) {
        return products.stream()
                .filter(p -> p.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product not found"));
    }

    private Order saveOrderAndProducts(Order order, List<OrderProduct> orderProducts) {
        Order savedOrder = orderRepository.save(order);
        order.updateTotalPrice();
        order.updateOrderName();
        orderProductRepository.saveAll(orderProducts);
        return savedOrder;
    }

    private void applyCouponIfPresent(Order order, List<Long> couponIds) {
        if (couponIds != null && !couponIds.isEmpty()) {
            issuedCouponRepository.findById(couponIds.get(0))
                    .ifPresent(order::applyCoupon);
        }
    }

    private Double calculateOrderItemPrice(Double price, Integer quantity) {
        return price * quantity;
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
