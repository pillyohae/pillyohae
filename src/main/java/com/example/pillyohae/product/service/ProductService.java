package com.example.pillyohae.product.service;

import com.example.pillyohae.product.dto.*;
import com.example.pillyohae.product.entity.Product;
import com.example.pillyohae.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductCreateResponseDto createProduct(ProductCreateRequestDto requestDto) {

        Product savedProduct = productRepository.save(new Product(
            requestDto.getProductName(),
            requestDto.getCategory(),
            requestDto.getDescription(),
            requestDto.getCompanyName(),
            requestDto.getPrice(),
            requestDto.getImageUrl(),
            requestDto.getStatus()
        ));

        return new ProductCreateResponseDto(
            savedProduct.getProductId(),
            savedProduct.getProductName(),
            savedProduct.getCategory(),
            savedProduct.getCompanyName(),
            savedProduct.getDescription(),
            savedProduct.getPrice(),
            savedProduct.getImageUrl(),
            savedProduct.getStatus());
    }

    public ProductUpdateResponseDto updateProduct(Long productId, ProductUpdateRequestDto requestDto) {

        Product findProduct = productRepository.findById(productId).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "product doesn't exist")
        );
        findProduct.updateProduct(
            requestDto.getProductName(),
            requestDto.getCategory(),
            requestDto.getDescription(),
            requestDto.getCompanyName(),
            requestDto.getPrice(),
            requestDto.getImageUrl(),
            requestDto.getStatus()
        );

        Product updatedProduct = productRepository.save(findProduct);

        return new ProductUpdateResponseDto(
            updatedProduct.getProductId(),
            updatedProduct.getProductName(),
            updatedProduct.getCategory(),
            updatedProduct.getDescription(),
            updatedProduct.getCompanyName(),
            updatedProduct.getPrice(),
            updatedProduct.getImageUrl(),
            updatedProduct.getStatus()
        );
    }

    public ProductGetResponseDto getProduct(Long productId) {
        Product findProduct = productRepository.findById(productId).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "product doesn't exist")
        );

        return new ProductGetResponseDto(
            findProduct.getProductId(),
            findProduct.getProductName(),
            findProduct.getCategory(),
            findProduct.getDescription(),
            findProduct.getCompanyName(),
            findProduct.getPrice(),
            findProduct.getImageUrl(),
            findProduct.getStatus()
        );
    }

    public Product findById(Long productId) {
        return productRepository.findById(productId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "아이디에 해당하는 상품이 존재하지 않습니다."));
    }
}
