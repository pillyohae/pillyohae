package com.example.main.product.dto.category;

import lombok.Getter;

@Getter
public class CategoryResponseDto {
    private Long categoryId;
    private String name;

    public CategoryResponseDto(Long categoryId, String name) {
        this.categoryId = categoryId;
        this.name = name;
    }
}
