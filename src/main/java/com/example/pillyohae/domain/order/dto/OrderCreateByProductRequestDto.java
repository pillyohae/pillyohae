package com.example.pillyohae.domain.order.dto;

import lombok.Getter;

@Getter
public class OrderCreateByProductRequestDto {
    private Long productId;
    private Long quantity;
}
