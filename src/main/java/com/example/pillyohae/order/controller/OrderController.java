package com.example.pillyohae.order.controller;

import com.example.pillyohae.order.dto.*;
import com.example.pillyohae.order.entity.status.OrderProductStatus;
import com.example.pillyohae.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/orderItems/{orderItemId}/status")
    public ResponseEntity<OrderItemStatusChangeResponseDto> changeOrderItemStatus(
        Authentication authentication,
        @PathVariable(name = "orderItemId") Long orderItemId,
        @RequestParam OrderProductStatus orderProductStatus) {
        return ResponseEntity.ok(
            orderService.changeOrderItemStatus(authentication.getName(), orderItemId,
                    orderProductStatus));
    }

}
