package com.example.pillyohae.order.service;

import com.example.pillyohae.cart.repository.CartRepository;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
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


    /**
     * product 들에 대한 검증을 먼저 하고
     * 주문 생성에 필요한 정보를 만들고
     * order entity를 생성합니다. 그리고 orderProduct를 생성하고 저장합니다.
     * requestDto에서 쿠폰이 있다면 쿠폰을 order에 적용합니다.
     *
     * @param email      유저 email
     * @param requestDto 주문정보 및 쿠폰정보
     * @return 생성된 order entity의 id
     */
    @Transactional
    public OrderDetailResponseDto createOrderByProducts(String email, OrderCreateRequestDto requestDto) {

        User user = userService.findByEmail(email);

        List<Product> purchaseProducts = fetchProducts(requestDto.getProductInfos());

        validateProducts(purchaseProducts);

        Order order = createOrder(user, purchaseProducts, requestDto.getProductInfos());

        List<OrderProduct> orderProducts = createOrderProducts(purchaseProducts, requestDto.getProductInfos(), order);
        // order와 orderProduct를 저장할때 order에 총액과 orderName을 설정합니다.
        Order savedOrder = orderRepository.save(order);

        orderProductRepository.saveAll(orderProducts);

        applyCouponIfPresent(savedOrder, requestDto.getCouponIds());

        OrderDetailResponseDto.OrderInfoDto orderInfoDto = new OrderDetailResponseDto.OrderInfoDto(order.getId(),
                order.getStatus(),order.getOrderName(), order.getTotalPrice(),order.getPaidAt(),
                order.getImageUrl(),order.getShippingAddress());

        List<OrderDetailResponseDto.OrderProductDto> orderProductDto = orderProducts.stream().map(orderProduct ->
                new OrderDetailResponseDto.OrderProductDto(
                        orderProduct.getId(),
                        orderProduct.getProductName(),
                        orderProduct.getQuantity(),
                        orderProduct.getPrice(),
                        orderProduct.getStatus()
                )).toList();

        return new OrderDetailResponseDto(orderInfoDto, orderProductDto);
    }

    // buyer의 order 내역 조회
    @Transactional
    public OrderPageResponseDto findOrders(String email, LocalDateTime startAt, LocalDateTime endAt, Long pageNumber, Long pageSize) {

        User user = userService.findByEmail(email);

        if (pageNumber < 0 || pageSize <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid pagination parameters");
        }

        List<OrderPageResponseDto.OrderInfoDto> orderInfoDtoList = orderRepository.findOrders(user.getId(), startAt, endAt, pageNumber, pageSize);

        OrderPageResponseDto.PageInfo pageInfo = new OrderPageResponseDto.PageInfo(pageNumber, pageSize);

        return new OrderPageResponseDto(orderInfoDtoList, pageInfo);

    }

    @Transactional
    public OrderPageSellerResponseDto findSellerOrders(String email, LocalDateTime startAt, LocalDateTime endAt, Long pageNumber, Long pageSize) {

        User user = userService.findByEmail(email);

        if (pageNumber < 0 || pageSize <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid pagination parameters");
        }

        List<OrderPageSellerResponseDto.OrderInfoDto> orderInfoDtoList = orderRepository.findSellerOrders(user.getId(), startAt, endAt, pageNumber, pageSize);

        OrderPageSellerResponseDto.PageInfo pageInfo = new OrderPageSellerResponseDto.PageInfo(pageNumber, pageSize);

        return new OrderPageSellerResponseDto(orderInfoDtoList, pageInfo);

    }

    // 주문 정보와 주문 상품 정보를 따로 조회
    @Transactional
    public OrderDetailResponseDto findOrderDetail(String email, UUID orderId) {

        User user = userService.findByEmail(email);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        if (OrderStatus.PENDING.equals(order.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Order is in pending");
        }

        if (!order.getUser().equals(user)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Order is not owned by user");
        }

        List<OrderDetailResponseDto.OrderProductDto> productInfos = orderRepository.findOrderProductsByOrderId(orderId);

        OrderDetailResponseDto.OrderInfoDto orderInfoDto = orderRepository.findOrderDetailOrderInfoDtoByOrderId(orderId);

        return new OrderDetailResponseDto(orderInfoDto, productInfos);

    }

    @Transactional
    public OrderDetailSellerResponseDto findOrderDetailSeller(String email, UUID orderId) {

        User user = userService.findByEmail(email);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        if (OrderStatus.PENDING.equals(order.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Order is in pending");
        }

        OrderDetailSellerResponseDto.OrderProductDto productInfo = orderRepository.findOrderDetailSellerProductDtoByOrderId(orderId,user.getId());

        OrderDetailSellerResponseDto.OrderInfoDto orderInfoDto = orderRepository.findOrderDetailSellerInfoDtoByOrderId(orderId);

        if (productInfo == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "들어온 주문이 없습니다");
        }

        return new OrderDetailSellerResponseDto(orderInfoDto,productInfo);

    }

    // seller orderItem 상태 수정
    @Transactional
    public OrderItemStatusChangeResponseDto changeOrderItemStatus(String email, Long orderItemId, OrderProductStatus newStatus) {

        User seller = userService.findByEmail(email);

        OrderProduct orderProduct = orderProductRepository.findById(orderItemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order item not found"));

        if (!seller.equals(orderProduct.getSeller())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Order item is not owned by user");
        }

        orderProduct.updateStatus(newStatus);

        return new OrderItemStatusChangeResponseDto(orderProduct.getId(), orderProduct.getStatus().getValue());
    }

    @Transactional
    public void updateOrderPaid (UUID orderId){
        Order paidOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        paidOrder.paid();
    }

    private List<Product> fetchProducts(List<OrderCreateRequestDto.ProductOrderInfo> productInfos) {
        List<Long> productIds = productInfos.stream()
                .map(OrderCreateRequestDto.ProductOrderInfo::getProductId)
                .toList();

        return productRepository.findByProductIdInJoinImage(productIds);
    }

    private void validateProducts(List<Product> products) {

        if (products.isEmpty()) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "There is no product to order");
        }
    }

    private Order createOrder(User user, List<Product> products, List<OrderCreateRequestDto.ProductOrderInfo> productOrderInfos) {
        // TODO: Replace dummy address with actual user address in the future
        ShippingAddress shippingAddress = createShippingAddress();
        String orderName = makeOrderName(products, productOrderInfos);
        String imageUrl = "no image";
        if (products.get(0).getImages() != null && !products.get(0).getImages().isEmpty()) {
            imageUrl = products.get(0).getImages().get(0).getFileUrl();
        }
        Long totalPrice = calculateTotalPrice(productOrderInfos, products);
        return new Order(user, shippingAddress, imageUrl, totalPrice, orderName);
    }

    private Long calculateTotalPrice(List<OrderCreateRequestDto.ProductOrderInfo> productOrderInfos, List<Product> products) {
        Long totalPrice = 0L;
        // 각 제품의 수량과 가격을 매칭하여 총액을 계산합니다
        for (OrderCreateRequestDto.ProductOrderInfo productOrderInfo : productOrderInfos) {
            Product matchingProduct = products.stream()
                    .filter(product -> product.getProductId().equals(productOrderInfo.getProductId()))
                    .findFirst()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product not found"));
            totalPrice += matchingProduct.getPrice() * productOrderInfo.getQuantity();
        }
        return totalPrice;
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
        String imageUrl = "no image";
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            imageUrl = product.getImages().get(0).getFileUrl();
        }
        return new OrderProduct(
                productInfo.getQuantity(),
                product.getPrice(),
                product.getProductId(),
                product.getUser(),
                // 제품의 가장 첫 사진을 저장
                imageUrl,
                order
        );
    }

    private Product findProductById(List<Product> products, Long productId) {
        return products.stream()
                .filter(p -> p.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product not found"));
    }

    public String makeOrderName(List<Product> products, List<OrderCreateRequestDto.ProductOrderInfo> productInfos) {
        Product firstProduct = products.get(0);
        int productCount = products.size();
        int firstProductQuantity = productInfos.stream().filter(productOrderInfo -> Objects.equals(productOrderInfo.getProductId(), products.get(0).getProductId()))
                .findFirst().orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product info not found")).getQuantity();
        return formatOrderName(firstProduct.getProductName(), firstProductQuantity, productCount);
    }

    private String formatOrderName(String firstProductName, int quantity, int totalProducts) {
        return totalProducts == 1
                ? String.format("%s %d개", firstProductName, quantity)
                : String.format("%s %d개 외 %d건", firstProductName, quantity, totalProducts - 1);
    }

    private void applyCouponIfPresent(Order order, List<Long> couponIds) {
        if (couponIds != null && !couponIds.isEmpty()) {

            issuedCouponRepository.findById(couponIds.get(0)).ifPresent(order::applyCoupon);
        }
    }


}
