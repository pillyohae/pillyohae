package com.example.pillyohae.product.controller;

import com.example.pillyohae.product.dto.*;
import com.example.pillyohae.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductCreateResponseDto> createProduct(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestBody ProductCreateRequestDto requestDto
    ) {

        SecurityContextHolder.getContext();

        ProductCreateResponseDto responseDto = productService.createProduct(requestDto, userDetails.getUsername());
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

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(
        @PathVariable Long productId,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        productService.deleteProduct(productId, userDetails.getUsername());
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
