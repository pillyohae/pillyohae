package com.example.common.product.entity.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ProductStatus {
    SELLING("selling"),
    DELETED("deleted"),
    SOLD_OUT("sold_out");

    private final String value;
}
