package com.example.pillyohae.product.controller;

import com.example.pillyohae.product.dto.*;
import com.example.pillyohae.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping("/products")
    public ResponseEntity<ProductCreateResponseDto> createProduct(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestBody ProductCreateRequestDto requestDto
    ) {

        SecurityContextHolder.getContext();

        ProductCreateResponseDto responseDto = productService.createProduct(requestDto, userDetails.getUsername());
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PutMapping("/products/{productId}")
    public ResponseEntity<ProductUpdateResponseDto> updateProduct(
        @PathVariable Long productId,
        @RequestBody ProductUpdateRequestDto requestDto
    ) {
        ProductUpdateResponseDto responseDto = productService.updateProduct(productId, requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<ProductGetResponseDto> getProduct(
        @PathVariable Long productId
    ) {
        ProductGetResponseDto responseDto = productService.getProduct(productId);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<Void> deleteProduct(
        @PathVariable Long productId,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        productService.deleteProduct(productId, userDetails.getUsername());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/products/search")
    public ResponseEntity<Page<ProductSearchResponseDto>> getAllProduct(
        @RequestParam(required = false) String productName,
        @RequestParam(required = false) String companyName,
        @RequestParam(required = false) String category,
        @RequestParam(value = "page", required = false, defaultValue = "1") int page,
        @RequestParam(value = "size", required = false, defaultValue = "20") int size,
        @RequestParam(value = "sortBy", required = false, defaultValue = "productId") String sortBy,
        @RequestParam(value = "isAsc", required = false, defaultValue = "false") boolean isAsc
    ) {
        page = page - 1;
        Page<ProductSearchResponseDto> searchProducts = productService.searchAndConvertProducts(productName, companyName, category, page, size, sortBy, isAsc);
        return new ResponseEntity<>(searchProducts, HttpStatus.OK);
    }

    @GetMapping("/users/sellers/products")
    public ResponseEntity<Page<ProductSearchResponseDto>> getSellersProducts(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestParam(value = "page", required = false, defaultValue = "1") int page,
        @RequestParam(value = "size", required = false, defaultValue = "20") int size,
        @RequestParam(value = "sortBy", required = false, defaultValue = "productId") String sortBy,
        @RequestParam(value = "isAsc", required = false) boolean isAsc
    ) {
        page = page - 1;
        Page<ProductSearchResponseDto> findSellersProducts = productService.findSellersProducts(userDetails.getUsername(), page, size, sortBy, isAsc);
        return new ResponseEntity<>(findSellersProducts, HttpStatus.OK);
    }


}
