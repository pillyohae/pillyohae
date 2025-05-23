package com.example.main.persona.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PersonaMessageDto {

    private String message;

    public PersonaMessageDto(String message) {
        this.message = message;
    }

}
