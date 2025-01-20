package com.example.pillyohae.global.message_queue.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.json.simple.JSONObject;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PaymentMessage implements DomainMessage {
    private JSONObject jsonObject;

    @Override
    public String getDomainType() {
        return "payment";
    }
}