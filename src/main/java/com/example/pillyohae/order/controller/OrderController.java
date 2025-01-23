package com.example.pillyohae.order.controller;

import com.example.pillyohae.order.dto.*;
import com.example.pillyohae.order.entity.status.OrderProductStatus;
import com.example.pillyohae.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     *
     * @param authentication
     * @param requestDto
     * @return
     */
    @PostMapping
    public ResponseEntity<OrderDetailResponseDto> createOrder(
            Authentication authentication, @RequestBody @Valid OrderCreateRequestDto requestDto) {

        return ResponseEntity.ok(orderService.createOrderByProducts(authentication.getName(), requestDto));

    }

    /**
     * 판매자의 주문 품목 상태 변경
     * @param authentication
     * @param orderItemId
     * @param orderProductStatus
     * @return
     */
    @PutMapping("/orderItems/{orderItemId}/status")
    public ResponseEntity<OrderItemStatusChangeResponseDto> changeOrderItemStatus(
        Authentication authentication,
        @PathVariable(name = "orderItemId") Long orderItemId,
        @RequestParam OrderProductStatus orderProductStatus) {
        return ResponseEntity.ok(
            orderService.changeOrderItemStatus(authentication.getName(), orderItemId,
                    orderProductStatus));
    }

    /**
     * 구매자의 주문 취소
     * @param authentication
     * @param orderId
     * @return
     */
    @DeleteMapping("/{orderId}/cancel")
    public ResponseEntity<OrderDetailResponseDto> cancelOrder(
            Authentication authentication,
            @PathVariable(name = "orderId") UUID orderId
    ){
        return ResponseEntity.ok(orderService.cancelOrder(authentication.getName(),orderId));
    }

    /**
     * 구매자의 환불 요청
     * @param authentication
     * @param orderId
     * @param orderProductId
     * @return
     */
    @DeleteMapping("/{orderId}/orderProducts/{orderProductId}")
    public ResponseEntity<OrderDetailResponseDto> refundOrderProduct(
            Authentication authentication,
            @PathVariable(name = "orderId") UUID orderId,
            @PathVariable(name = "orderProductId") Long orderProductId
    ){
        return ResponseEntity.ok(orderService.refundOrderProduct(authentication.getName(),orderId,orderProductId));
    }

}
