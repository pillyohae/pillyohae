package com.example.pillyohae.product.service;

import com.example.pillyohae.product.dto.ProductCreateRequestDto;
import com.example.pillyohae.product.dto.ProductCreateResponseDto;
import com.example.pillyohae.product.entity.Product;
import com.example.pillyohae.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
