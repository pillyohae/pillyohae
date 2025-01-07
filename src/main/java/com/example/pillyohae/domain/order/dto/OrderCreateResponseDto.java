package com.example.pillyohae.domain.order.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor(force = true)
public class OrderCreateResponseDto {
    private final UUID id;

    public OrderCreateResponseDto(UUID id) {
        this.id = id;
    }
}
