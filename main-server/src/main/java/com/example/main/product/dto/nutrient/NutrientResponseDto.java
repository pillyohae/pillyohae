package com.example.main.product.dto.nutrient;

import lombok.Getter;

@Getter
public class NutrientResponseDto {
    private Long nutrientId;
    private String name;
    private String description;

    public NutrientResponseDto(Long nutrientId, String name, String description) {
        this.nutrientId = nutrientId;
        this.name = name;
        this.description = description;
    }
}
