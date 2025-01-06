package com.example.pillyohae.product.controller;

import com.example.pillyohae.product.dto.ProductCreateRequestDto;
import com.example.pillyohae.product.dto.ProductCreateResponseDto;
import com.example.pillyohae.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
