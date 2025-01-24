package com.example.pillyohae.persona.dto;

import lombok.Getter;

@Getter
public class PersonaMessageCreateResponseDto {

    private final String message;

    public PersonaMessageCreateResponseDto(String message) {
        this.message = message;
    }
}
