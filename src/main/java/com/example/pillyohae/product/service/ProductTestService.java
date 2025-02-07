package com.example.pillyohae.product.service;

import com.example.pillyohae.product.dto.category.CategoryResponseDto;
import com.example.pillyohae.product.dto.nutrient.NutrientResponseDto;
import com.example.pillyohae.product.dto.product.ProductGetResponseDto;
import com.example.pillyohae.product.entity.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductTestService {

    private final ProductService productService;

    @Transactional
    public ProductGetResponseDto getProduct(Long productId) {

        Product findProduct = productService.findById(productId);

        //카테고리 정보 DTO로 변환
        CategoryResponseDto categoryResponseDto = new CategoryResponseDto(
            findProduct.getCategory().getCategoryId(),
            findProduct.getCategory().getName()
        );

        //영양성분 정보 DTO로 변환
        NutrientResponseDto nutrientResponseDto = new NutrientResponseDto(
            findProduct.getNutrient().getNutrientId(),
            findProduct.getNutrient().getName(),
            findProduct.getNutrient().getDescription()
        );

        List<ProductGetResponseDto.ImageResponseDto> images = findProduct.getImages()
            .stream()
            .map(image -> new ProductGetResponseDto.ImageResponseDto(
                image.getId(),
                image.getFileUrl(),
                image.getPosition()
            ))
            .toList();

        ProductGetResponseDto responseDto = new ProductGetResponseDto(
            findProduct.getProductId(),
            findProduct.getProductName(),
            categoryResponseDto,
            findProduct.getDescription(),
            findProduct.getCompanyName(),
            findProduct.getPrice(),
            findProduct.getStatus(),
            findProduct.getStock(),
            images,
            nutrientResponseDto
        );

        return responseDto;
    }

}
