package com.example.pillyohae.user.entity.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Status {
    ACTIVE("active"), WITHDRAW("withdraw");

    private final String value;
}
