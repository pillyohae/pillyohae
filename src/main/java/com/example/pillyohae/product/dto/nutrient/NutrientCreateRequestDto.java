package com.example.pillyohae.product.dto.nutrient;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class NutrientCreateRequestDto {
    @NotBlank
    private String name;

    private String description;
}
