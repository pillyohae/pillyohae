package com.example.pillyohae.global.message_queue.service;

import com.example.pillyohae.order.entity.Order;
import com.example.pillyohae.order.repository.OrderRepository;
import com.example.pillyohae.payment.entity.PayMethod;
import com.example.pillyohae.payment.entity.Payment;
import com.example.pillyohae.payment.entity.PaymentFailLog;
import com.example.pillyohae.payment.repository.PaymentFailLogRepository;
import com.example.pillyohae.payment.repository.PaymentRepository;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * 결제 요청 및 처리 서비스
 * <p>
 * - 결제 요청을 TossPayments API로 전달
 * - 결제 성공/실패 시 주문 상태 변경
 * - 실패한 결제 내역 저장
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentFailLogRepository paymentFailLogRepository;

    @Value("${toss.secret-key}")
    private String TOSS_SECRET_KEY;

    JSONParser parser = new JSONParser();

    /**
     * 결제 성공 시 주문 상태 변경
     *
     * @param tossResult 결제 승인 응답 데이터
     */
    private void success(JSONObject tossResult) {
        // 결제 성공 정보 저장
        Payment payment = new Payment(
            (String) tossResult.get("mid"),
            (String) tossResult.get("version"),
            (String) tossResult.get("paymentKey"),
            (String) tossResult.get("status"),
            UUID.fromString((String) tossResult.get("orderId")),
            (String) tossResult.get("orderName"),
            (String) tossResult.get("requestedAt"),
            (String) tossResult.get("approvedAt"),
            (Integer) tossResult.get("totalAmount"),
            (Integer) tossResult.get("balanceAmount"),
            Enum.valueOf(PayMethod.class, (String) tossResult.get("method"))
        );
        paymentRepository.save(payment);

        // 주문 상태 변경
        Order paidOrder = orderRepository.findById(payment.getOrderId())
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        paidOrder.paid();
    }

    /**
     * 결제 실패 시 주문 정보 삭제 및 로그 저장
     *
     * @param tossResult 결제 실패 응답 데이터
     */
    private void fail(JSONObject tossResult) {
        JSONObject errorObject = (JSONObject) tossResult.get("error");

        // 결제 실패 로그 저장
        PaymentFailLog paymentFailLog = new PaymentFailLog(
            (String) tossResult.get("version"),
            (String) tossResult.get("traceId"),
            (String) errorObject.get("code"),
            (String) errorObject.get("message")
        );

        paymentFailLogRepository.save(paymentFailLog);
        orderRepository.deleteById(UUID.fromString((String) tossResult.get("orderId")));
    }

    /**
     * TossPayments API를 이용해 결제 요청
     *
     * @param tossRequest 결제 요청 데이터
     * @throws IOException    네트워크 오류
     * @throws ParseException 응답 데이터 파싱 오류
     */
    @Transactional
    public void requestPayment(JSONObject tossRequest) throws IOException, ParseException {
        HttpURLConnection connection = getTossResult(tossRequest);

        int code = connection.getResponseCode();
        boolean isSuccess = code == 200;
        InputStream responseStream =
            isSuccess ? connection.getInputStream() : connection.getErrorStream();
        Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8);

        JSONObject tossResult = (JSONObject) parser.parse(reader);
        responseStream.close();

        if (!isSuccess) {
            fail(tossResult);
        } else {
            success(tossResult);
        }
    }

    // 토스페이먼츠 API는 시크릿 키를 사용자 ID로 사용하고, 비밀번호는 사용하지 않습니다.
    // 비밀번호가 없다는 것을 알리기 위해 시크릿 키 뒤에 콜론을 추가합니다.

    private HttpURLConnection getTossResult(JSONObject tossRequest) throws IOException {
        String widgetSecretKey = TOSS_SECRET_KEY;
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode(
            (widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
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

}

