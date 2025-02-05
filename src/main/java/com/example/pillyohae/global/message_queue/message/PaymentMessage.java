package com.example.pillyohae.global.message_queue.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.simple.JSONObject;

/**
 * 결제 메시지를 나타내는 클래스
 * <p>
 * {@link Message} 인터페이스를 구현하며, 결제 요청 정보를 포함함.
 */
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
