package com.example.pillyohae.product.dto;

import lombok.Getter;

@Getter
public class NutrientResponseDto {
    private Long id;
    private String name;
    private String description;

    public NutrientResponseDto(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}
