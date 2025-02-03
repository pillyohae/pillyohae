package com.example.pillyohae.payment.service;

import com.example.pillyohae.global.distributedLock.DistributedLock;
import com.example.pillyohae.global.message_queue.message.PaymentMessage;
import com.example.pillyohae.global.message_queue.publisher.MessagePublisher;
import com.example.pillyohae.order.dto.OrderProductFetchJoinProduct;
import com.example.pillyohae.order.repository.OrderProductRepository;
import com.example.pillyohae.order.repository.OrderRepository;
import com.example.pillyohae.order.service.OrderService;
import com.example.pillyohae.payment.repository.PaymentRepository;
import com.example.pillyohae.product.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.redisson.api.RedissonClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderService orderService;
    private final MessagePublisher orderMessagePublisher;
    private final ObjectMapper objectMapper;
    private final RedissonClient redissonClient;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;

    JSONParser parser = new JSONParser();

    @DistributedLock(key = "'order'", waitTime = 10L, leaseTime = 10L)
    public JSONObject pay(String jsonBody) throws IOException, ParseException {


        JSONObject tossRequest = makeTossRequest(jsonBody);


        List<OrderProductFetchJoinProduct> orderProductWithProductList = orderRepository.findOrderProductWithProduct(UUID.fromString(tossRequest.get("orderId").toString()));
        // 재고 확인
        for (OrderProductFetchJoinProduct orderProductWithProduct : orderProductWithProductList) {
            Integer quantity = orderProductWithProduct.getOrderProduct().getQuantity();
            Integer stock = orderProductWithProduct.getProduct().getStock();
            orderProductWithProduct.getProduct().deductStock(quantity);
            if (stock <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, orderProductWithProduct.getProduct().getProductName() + "제품이" + "재고가 모두 소진되었습니다 결제를 취소합니다");
            }
            // stock이 0보다 작거나 같아지면 되돌림
        }
        PaymentMessage paymentMessage = new PaymentMessage(tossRequest,"payment");
        orderMessagePublisher.directSendMessage(paymentMessage);

        // 일단 주문 성공을 했다고 알려준다. 이후 실제 결제되고 고객의 email에 결제 완료 메일을 보낸다.
        return tossRequest;
    }

    private JSONObject makeTossRequest(String jsonBody) {
        String orderId;
        String amount;
        String paymentKey;
        try {
            // 클라이언트에서 받은 JSON 요청 바디입니다.
            JSONObject requestData = (JSONObject) parser.parse(jsonBody);
            paymentKey = (String) requestData.get("paymentKey");
            orderId = (String) requestData.get("orderId");
            amount = (String) requestData.get("amount");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        ;
        JSONObject obj = new JSONObject();
        obj.put("orderId", orderId);
        obj.put("amount", amount);
        obj.put("paymentKey", paymentKey);

        return obj;
    }




}
