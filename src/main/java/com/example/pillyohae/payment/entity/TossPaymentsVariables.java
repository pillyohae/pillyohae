package com.example.pillyohae.payment.entity;

import lombok.Getter;

@Getter
public enum TossPaymentsVariables {
    MID("mId"),
    VERSION("version"),
    PAYMENTKEY("paymentKey"),
    STATUS("status"),
    LASTTRANSACTIONKEY("lastTransactionKey"),
    METHOD("method"),
    ORDERID("orderId"),
    ORDERNAME("orderName"),
    REQUESTEDAT("requestedAt"),
    APPROVEDAT("approvedAt"),
    USEESCROW("useEscrow"),
    CULTUREEXPENSE("cultureExpense"),
    CARD("card"),
    VIRTUALACCOUNT("virtualAccount"),
    TRANSFER("transfer"),
    MOBILEPHONE("mobilePhone"),
    GIFTCERTIFICATE("giftCertificate"),
    CASHRECEIPT("cashReceipt"),
    CASHRECEIPTS("cashReceipts"),
    DISCOUNT("discount"),
    CANCELS("cancels"),
    SECRET("secret"),
    TYPE("type"),
    EASYPAY("easyPay"),
    COUNTRY("country"),
    FAILURE("failure"),
    ISPARTIALCANCELABLE("isPartialCancelable"),
    RECEIPT("receipt"),
    CHECKOUT("checkout"),
    CURRENCY("currency"),
    TOTALAMOUNT("totalAmount"),
    BALANCEAMOUNT("balanceAmount"),
    SUPPLIEDAMOUNT("suppliedAmount"),
    VAT("vat"),
    TAXFREEAMOUNT("taxFreeAmount"),
    METADATA("metadata");

    private final String value;

    TossPaymentsVariables(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
