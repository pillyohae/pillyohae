package com.example.pillyohae.order.controller;

import com.example.pillyohae.order.dto.OrderCreateByProductRequestDto;
import com.example.pillyohae.order.dto.OrderCreateResponseDto;
import com.example.pillyohae.order.dto.SellerOrderItemStatusChangeResponseDto;
import com.example.pillyohae.order.entity.status.OrderItemStatus;
import com.example.pillyohae.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/products/create")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseEntity<OrderCreateResponseDto> createOrderByProduct(Authentication authentication,
                                                                       @RequestBody @Valid OrderCreateByProductRequestDto requestDto) {
        return ResponseEntity.ok(orderService.createOrderByProduct(authentication.getName(), requestDto));
    }

    @PostMapping("/carts/create")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseEntity<OrderCreateResponseDto> createOrderByCart(Authentication authentication){
        return ResponseEntity.ok(orderService.createOrderByCart(authentication.getName()));
    }

    @PutMapping("/orderItems/{orderItemId}/status")
    public ResponseEntity<SellerOrderItemStatusChangeResponseDto> changeOrderItemStatus(Authentication authentication,
                                                                                        @PathVariable(name = "orderItemId") Long orderItemId,
                                                                                        @RequestParam OrderItemStatus orderItemStatus) {
        return ResponseEntity.ok(orderService.changeOrderItemStatus(authentication.getName(), orderItemId, orderItemStatus));
    }

}
