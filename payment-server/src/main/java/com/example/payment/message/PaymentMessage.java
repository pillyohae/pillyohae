package com.example.payment.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.simple.JSONObject;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentMessage implements Message {
    private JSONObject tossRequest;
    private String domainType;

    @Override
    public String getDomainType() {
        return domainType;
    }
}