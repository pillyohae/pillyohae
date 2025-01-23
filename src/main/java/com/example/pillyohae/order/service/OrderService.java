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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
    public Page<OrderInfoDto> findOrders(String email, LocalDateTime startAt, LocalDateTime endAt, Integer pageNumber, Integer pageSize) {

        User user = userService.findByEmail(email);

        if (pageNumber < 0 || pageSize <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid pagination parameters");
        }
        Sort sort = Sort.by("paidAt").descending();
        return orderRepository.findOrders(user.getId(), startAt, endAt, PageRequest.of(pageNumber, pageSize, sort) );
    }

    @Transactional
    public Page<OrderSellerInfoDto> findSellerOrders(String email, LocalDateTime startAt, LocalDateTime endAt, Integer pageNumber, Integer pageSize) {

        User user = userService.findByEmail(email);

        if (pageNumber < 0 || pageSize <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid pagination parameters");
        }
        Sort sort = Sort.by("paidAt").descending();

        return orderRepository.findSellerOrders(user.getId(), startAt, endAt,PageRequest.of(pageNumber,pageSize,sort));
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

    /**
     * seller의 order 상세 내역
     * @param email
     * @param orderId
     * @return
     */
    @Transactional
    public OrderDetailSellerResponseDto findOrderDetailSeller(String email, UUID orderId) {

        User user = userService.findByEmail(email);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        if (OrderStatus.PENDING.equals(order.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Order is in pending");
        }

        List<OrderDetailSellerResponseDto.OrderProductDto> productInfo = orderRepository.findOrderDetailSellerProductDtoByOrderId(orderId,user.getId());

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

    /**
     * 배송 시작전 전체 주문 취소
     */
    @Transactional
    public OrderDetailResponseDto cancelOrder(String email, UUID orderId) {

        User user = userService.findByEmail(email);

        Order cancelOrder = orderRepository.findByOrderIdWithOrderProducts(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        if(!cancelOrder.getUser().equals(user)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Order is not owned by user");
        }

        // 이미 배송된 물품이 있을경우 주문취소 불가
        Optional<OrderProduct> shippingOrderProduct = cancelOrder.getOrderProducts().stream().filter(orderProduct -> !OrderProductStatus.CHECK_ORDER.equals(orderProduct.getStatus())).findAny();

        if(shippingOrderProduct.isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 배송된 물품이 있어 주문취소가 불가능 합니다.");
        }

        cancelOrder.updateStatus(OrderStatus.CANCELLED);

        OrderDetailResponseDto.OrderInfoDto orderInfoDto = new OrderDetailResponseDto.OrderInfoDto(cancelOrder.getId()
                ,cancelOrder.getStatus(),cancelOrder.getOrderName(),cancelOrder.getTotalPrice()
                ,cancelOrder.getPaidAt(),cancelOrder.getImageUrl(),cancelOrder.getShippingAddress());



        List<OrderDetailResponseDto.OrderProductDto> orderProductDtos = cancelOrder.getOrderProducts().stream()
                .map(orderProduct -> new OrderDetailResponseDto.OrderProductDto(orderProduct.getId(), orderProduct.getProductName(),orderProduct.getQuantity(), orderProduct.getPrice(),orderProduct.getStatus())).toList();

        return new OrderDetailResponseDto(orderInfoDto,orderProductDtos);
    }

    /**
     * 개별 물품 환불 신청
     **/
    @Transactional
    public OrderDetailResponseDto refundOrderProduct(String email, UUID orderId, Long orderProductId) {
        User user = userService.findByEmail(email);

        Order cancelOrder = orderRepository.findByOrderIdWithOrderProducts(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        // 이미 배송된 물품이 있을경우 주문취소 불가

        if(!cancelOrder.getUser().equals(user)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Order is not owned by user");
        }

        OrderProduct refundOrderProduct = cancelOrder.getOrderProducts().stream().filter(orderProduct -> orderProductId.equals(orderProduct.getId())).findAny()
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "OrderProduct not found")
                );

        refundOrderProduct.updateStatus(OrderProductStatus.RETURN_REQUESTED);

        OrderDetailResponseDto.OrderInfoDto orderInfoDto = new OrderDetailResponseDto.OrderInfoDto(cancelOrder.getId()
                ,cancelOrder.getStatus(),cancelOrder.getOrderName(),cancelOrder.getTotalPrice()
                ,cancelOrder.getPaidAt(),cancelOrder.getImageUrl(),cancelOrder.getShippingAddress());



        List<OrderDetailResponseDto.OrderProductDto> orderProductDtos = cancelOrder.getOrderProducts().stream()
                .map(orderProduct -> new OrderDetailResponseDto.OrderProductDto(orderProduct.getId(), orderProduct.getProductName(),orderProduct.getQuantity(), orderProduct.getPrice(),orderProduct.getStatus())).toList();

        return new OrderDetailResponseDto(orderInfoDto,orderProductDtos);
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
        // 유저 정보에서 주소를 가져옴
        ShippingAddress shippingAddress = user.getAddress();

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

    private void applyCouponIfPresent(Order order, List<UUID> couponIds) {
        if (couponIds != null && !couponIds.isEmpty()) {

            issuedCouponRepository.findById(couponIds.get(0)).ifPresent(order::applyCoupon);
        }
    }


}
