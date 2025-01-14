package com.example.pillyohae.order.controller;

import com.example.pillyohae.order.dto.*;
import com.example.pillyohae.order.entity.status.OrderItemStatus;
import com.example.pillyohae.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/products/create")
    public ResponseEntity<OrderCreateResponseDto> createOrderByProduct(
        Authentication authentication,
        @RequestBody @Valid OrderCreateByProductRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(orderService.createOrderByProduct(authentication.getName(), requestDto));
    }

    @PostMapping("/carts/create")
    public ResponseEntity<OrderCreateResponseDto> createOrderByCart(Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(orderService.createOrderByCart(authentication.getName()));
    }

    @PutMapping("/orderItems/{orderItemId}/status")
    public ResponseEntity<SellerOrderItemStatusChangeResponseDto> changeOrderItemStatus(
        Authentication authentication,
        @PathVariable(name = "orderItemId") Long orderItemId,
        @RequestParam OrderItemStatus orderItemStatus) {
        return ResponseEntity.ok(
            orderService.changeOrderItemStatus(authentication.getName(), orderItemId,
                orderItemStatus));
    }
    // 쿠폰 사용
    @PatchMapping ("/{orderId}/coupon")
    public ResponseEntity<OrderUseCouponResponseDto> useCoupon(
            Authentication authentication,
            @PathVariable(name = "orderId") UUID orderId,
            @RequestParam(name = "couponId") Long couponId) {
        return ResponseEntity.ok(
                orderService.useCoupon(authentication.getName(),orderId,couponId));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<BuyerOrderDetailInfo> getOrderDetailBeforePayment(
            Authentication authentication,
            @PathVariable(name = "orderId") UUID orderId) {
        return ResponseEntity.ok(orderService.getOrderDetailBeforePayment(authentication.getName(), orderId));
    }


}
