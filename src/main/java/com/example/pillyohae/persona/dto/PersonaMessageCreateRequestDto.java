package com.example.pillyohae.persona.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PersonaMessageCreateRequestDto {

    @NotBlank(message = "ingredient는 null 이거나 빈 값일 수 없습니다.")
    private final String ingredient;

    public PersonaMessageCreateRequestDto(String ingredient) {
        this.ingredient = ingredient;
    }
}
