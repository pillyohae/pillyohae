package com.example.pillyohae.global.message_queue.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.simple.JSONObject;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentMessage {
    private JSONObject jsonObject;
    private String domainType;
}