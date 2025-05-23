package com.example.main.persona.dto;

import lombok.Getter;

@Getter
public class PersonaMessageCreateResponseDto {

    private final String message;

    public PersonaMessageCreateResponseDto(String message) {
        this.message = message;
    }
}
