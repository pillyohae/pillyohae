package com.example.pillyohae.order.controller;

import com.example.pillyohae.order.dto.OrderCreateRequestDto;
import com.example.pillyohae.order.dto.OrderDetailResponseDto;
import com.example.pillyohae.order.dto.OrderItemStatusChangeResponseDto;
import com.example.pillyohae.order.entity.status.OrderProductStatus;
import com.example.pillyohae.order.service.OrderService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * @param userDetails
     * @param requestDto
     * @return
     */
    @PostMapping
    public ResponseEntity<OrderDetailResponseDto> createOrder(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestBody @Valid OrderCreateRequestDto requestDto
    ) {

        return ResponseEntity.ok(
            orderService.createOrderByProducts(userDetails.getUsername(), requestDto));

    }

    /**
     * 판매자의 주문 품목 상태 변경
     *
     * @param userDetails
     * @param orderItemId
     * @param orderProductStatus
     * @return
     */
    @PutMapping("/orderItems/{orderItemId}/status")
    public ResponseEntity<OrderItemStatusChangeResponseDto> changeOrderItemStatus(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable(name = "orderItemId") Long orderItemId,
        @RequestParam OrderProductStatus orderProductStatus) {
        return ResponseEntity.ok(
            orderService.changeOrderItemStatus(userDetails.getUsername(), orderItemId,
                orderProductStatus));
    }

    /**
     * 구매자의 주문 취소
     *
     * @param userDetails
     * @param orderId
     * @return
     */
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<OrderDetailResponseDto> cancelOrder(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable(name = "orderId") UUID orderId
    ) {
        return ResponseEntity.ok(orderService.cancelOrder(userDetails.getUsername(), orderId));
    }

    /**
     * 구매자의 환불 요청
     *
     * @param userDetails
     * @param orderId
     * @param orderProductId
     * @return
     */
    @PutMapping("/{orderId}/orderProducts/{orderProductId}")
    public ResponseEntity<OrderDetailResponseDto> refundOrderProduct(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable(name = "orderId") UUID orderId,
        @PathVariable(name = "orderProductId") Long orderProductId
    ) {
        return ResponseEntity.ok(
            orderService.refundOrderProduct(userDetails.getUsername(), orderId, orderProductId));
    }

}
