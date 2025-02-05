package com.example.pillyohae.payment.service;

import com.example.pillyohae.global.distributedLock.OrderDistributedLock;
import com.example.pillyohae.global.message_queue.message.PaymentMessage;
import com.example.pillyohae.global.message_queue.publisher.MessagePublisher;
import com.example.pillyohae.order.dto.OrderProductFetchJoinProduct;
import com.example.pillyohae.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

/**
 * 결제 처리 서비스
 * <p>
 * - 주문 정보를 조회하고, 재고 확인 후 결제 요청
 * - 결제 성공 시 메시지를 발행하여 결제 처리를 진행
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final MessagePublisher orderMessagePublisher;
    private final OrderRepository orderRepository;

    JSONParser parser = new JSONParser();

    /**
     * 결제 요청을 처리하는 메서드
     *
     * @param jsonBody 클라이언트에서 전달된 결제 정보 (JSON 문자열)
     * @return TossPayments 결제 요청 JSON 객체
     */
    @OrderDistributedLock(key = "#jsonBody", waitTime = 60L, leaseTime = 60L)
    public JSONObject pay(String jsonBody) {
        // TossPayments 결제 요청 데이터 생성
        JSONObject tossRequest = makeTossRequest(jsonBody);

        // 주문 정보 조회 및 재고 확인
        List<OrderProductFetchJoinProduct> orderProductWithProductList =
            orderRepository.findOrderProductWithProduct(
                UUID.fromString(tossRequest.get("orderId").toString()));

        for (OrderProductFetchJoinProduct orderProductWithProduct : orderProductWithProductList) {
            Integer quantity = orderProductWithProduct.getOrderProduct().getQuantity();
            Integer stock = orderProductWithProduct.getProduct().getStock();
            orderProductWithProduct.getProduct().deductStock(quantity);

            // 재고 부족 시 예외 발생
            if (stock <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    orderProductWithProduct.getProduct().getProductName()
                        + " 제품의 재고가 부족하여 결제가 취소됩니다.");
            }
        }

        // 결제 메시지를 생성하여 발행
        PaymentMessage paymentMessage = new PaymentMessage(tossRequest, "payment");
        orderMessagePublisher.directSendMessage(paymentMessage);

        // 결제 요청 반환 (클라이언트에 성공 응답)
        return tossRequest;
    }

    /**
     * TossPayments 결제 요청 JSON 객체 생성
     *
     * @param jsonBody 클라이언트에서 전달된 결제 요청 데이터
     * @return TossPayments 결제 요청 JSON 객체
     */
    private JSONObject makeTossRequest(String jsonBody) {
        try {
            JSONObject requestData = (JSONObject) parser.parse(jsonBody);
            JSONObject obj = new JSONObject();
            obj.put("orderId", requestData.get("orderId"));
            obj.put("amount", requestData.get("amount"));
            obj.put("paymentKey", requestData.get("paymentKey"));
            return obj;
        } catch (ParseException e) {
            throw new RuntimeException("결제 요청 JSON 파싱 실패", e);
        }
    }
}
