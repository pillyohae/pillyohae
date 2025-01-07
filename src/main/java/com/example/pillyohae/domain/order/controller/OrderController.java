package com.example.pillyohae.domain.order.controller;

import com.example.pillyohae.domain.order.dto.OrderCreateByProductRequestDto;
import com.example.pillyohae.domain.order.dto.OrderCreateResponseDto;
import com.example.pillyohae.domain.order.service.OrderService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/products/orders/create")
    public ResponseEntity<OrderCreateResponseDto> createOrderByProduct(Authentication authentication,
                                                                       @RequestBody OrderCreateByProductRequestDto requestDto) {
        return ResponseEntity.ok(orderService.createOrderByProduct(authentication.getName(), requestDto));
    }


}
