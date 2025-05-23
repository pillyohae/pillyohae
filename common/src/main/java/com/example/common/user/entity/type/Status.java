package com.example.common.user.entity.type;

import lombok.Generated;

public enum Status {
    ACTIVE("active"),
    WITHDRAW("withdraw");

    private final String value;

    @Generated
    private Status(final String value) {
        this.value = value;
    }
}
