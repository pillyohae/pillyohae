package com.example.pillyohae.user.entity.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Role {
    USER("user"), SELLER("seller");

    private final String name;
}
