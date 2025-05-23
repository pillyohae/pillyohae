package com.example.common.payment.entity.toss_variable;

import lombok.Getter;

@Getter
public enum TossPaymentsCardVariables {
    ISSUERCODE("issuerCode"),
    ACQUIRERCODE("acquirerCode"),
    NUMBER("number"),
    INSTALLMENTPLANMONTHS("installmentPlanMonths"),
    ISINTERESTFREE("isInterestFree"),
    INTERESTPAYER("interestPayer"),
    APPROVENO("approveNo"),
    USECARDPOINT("useCardPoint"),
    CARDTYPE("cardType"),
    OWNERTYPE("ownerType"),
    ACQUIRESTATUS("acquireStatus"),
    AMOUNT("amount");

    private final String value;

    TossPaymentsCardVariables(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}