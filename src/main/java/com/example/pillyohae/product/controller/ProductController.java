package com.example.pillyohae.product.controller;

import com.example.pillyohae.product.dto.*;
import com.example.pillyohae.product.entity.Product;
import com.example.pillyohae.product.service.ProductService;
import jakarta.persistence.Column;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;


    @PostMapping
    public ResponseEntity<ProductCreateResponseDto> createProduct(
            @RequestBody ProductCreateRequestDto requestDto
            ) {
        ProductCreateResponseDto responseDto = productService.createProduct(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductUpdateResponseDto> updateProduct(
            @PathVariable Long productId,
            @RequestBody ProductUpdateRequestDto requestDto
    ) {
        ProductUpdateResponseDto responseDto = productService.updateProduct(productId, requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductGetResponseDto> getProduct(
            @PathVariable Long productId
    ) {
        ProductGetResponseDto responseDto = productService.getProduct(productId);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }



}
