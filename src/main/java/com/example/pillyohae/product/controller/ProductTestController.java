package com.example.pillyohae.product.controller;

import com.example.pillyohae.product.dto.product.ProductGetResponseDto;
import com.example.pillyohae.product.service.ProductTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class ProductTestController {

    private final ProductTestService productTestService;

    @GetMapping("/products/without-redis/{productId}")
    public ResponseEntity<ProductGetResponseDto> getProduct(
        @PathVariable Long productId
    ) {
        ProductGetResponseDto responseDto = productTestService.getProduct(productId);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
