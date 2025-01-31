package com.example.pillyohae.payment.service;

import com.example.pillyohae.global.message_queue.message.PaymentMessage;
import com.example.pillyohae.global.message_queue.publisher.MessagePublisher;
import com.example.pillyohae.order.dto.OrderProductFetchJoinProduct;
import com.example.pillyohae.order.repository.OrderProductRepository;
import com.example.pillyohae.order.repository.OrderRepository;
import com.example.pillyohae.order.service.OrderService;
import com.example.pillyohae.payment.repository.PaymentRepository;
import com.example.pillyohae.product.entity.Product;
import com.example.pillyohae.product.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderService orderService;
    private final MessagePublisher redisMessagePublisher;
    private final ObjectMapper objectMapper;
    private final RedissonClient redissonClient;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;

    JSONParser parser = new JSONParser();


    @Value("${toss.secret-key}")
    private String TOSS_SECRET_KEY;


    public ResponseEntity<JSONObject> pay(String jsonBody) throws IOException, ParseException {


        JSONObject tossRequest = makeTossRequest(jsonBody);

        RLock lock = redissonClient.getLock("order-lock");
        boolean isLocked = false;

        try {
            isLocked = lock.tryLock(60, TimeUnit.SECONDS);
            if (isLocked) {
                checkStockAndDeduct(tossRequest);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new IOException(e.getMessage());
        }

        HttpURLConnection connection = getTossResult(tossRequest);


        // 결제 승인 요청 후 결과를 받는데 0.668초 정도 소요
        int code = connection.getResponseCode();
        boolean isSuccess = code == 200;
        InputStream responseStream = isSuccess ? connection.getInputStream() : connection.getErrorStream();
        Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8);

        JSONObject tossResult = (JSONObject) parser.parse(reader);
        // 결제 실패 에러 발생 처리
        if (!isSuccess) {
            log.info(responseStream.toString());
            return ResponseEntity.status(code).body(tossResult);
        }

        responseStream.close();

        // 결제 성공시 주문 결제완료로 변경 및 결제 로그 저장
        PaymentMessage paymentMessage = new PaymentMessage(tossResult, "payment");
        String message = objectMapper.writeValueAsString(paymentMessage);
        redisMessagePublisher.publish(message);

        return ResponseEntity.status(code).body(tossResult);
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

    // 토스페이먼츠 API는 시크릿 키를 사용자 ID로 사용하고, 비밀번호는 사용하지 않습니다.
    // 비밀번호가 없다는 것을 알리기 위해 시크릿 키 뒤에 콜론을 추가합니다.
    private HttpURLConnection getTossResult(JSONObject tossRequest) throws IOException {
        String widgetSecretKey = TOSS_SECRET_KEY;
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        String authorizations = "Basic " + new String(encodedBytes);

        // 결제를 승인하면 결제수단에서 금액이 차감돼요.
        URL url = new URL("https://api.tosspayments.com/v1/payments/confirm");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Accept-Language", "en-US");
        connection.setRequestProperty("Authorization", authorizations);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(tossRequest.toString().getBytes(StandardCharsets.UTF_8));
        return connection;
    }

    @Transactional
    protected void checkStockAndDeduct(JSONObject tossRequest) {
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
        List<Product> products = orderProductWithProductList.stream().map(OrderProductFetchJoinProduct::getProduct).toList();
        productRepository.saveAll(products);
    }
}
