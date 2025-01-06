package com.example.pillyohae.product.service;

import com.example.pillyohae.product.dto.ProductCreateRequestDto;
import com.example.pillyohae.product.dto.ProductCreateResponseDto;
import com.example.pillyohae.product.dto.ProductUpdateRequestDto;
import com.example.pillyohae.product.dto.ProductUpdateResponseDto;
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
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "card doesn't exist")
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
}
