package com.example.pillyohae.cart.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CartCreateResponseDto {

    private final Long cartId;

    private final LocalDateTime createdAt;
}
