package com.example.pillyohae.payment.entity;

import lombok.Getter;

@Getter
public enum EasyPayVariables {
    간편결제("provider"),
    AMOUNT("amount"),
    DISCOUNTAMOUNT("discountAmount");
    private String value;
    private EasyPayVariables(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
