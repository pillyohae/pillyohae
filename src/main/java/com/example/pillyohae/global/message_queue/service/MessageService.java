package com.example.pillyohae.global.message_queue.service;

import com.example.pillyohae.global.message_queue.message.PaymentMessage;
import com.example.pillyohae.order.entity.Order;
import com.example.pillyohae.order.repository.OrderRepository;
import com.example.pillyohae.order.service.OrderService;
import com.example.pillyohae.payment.entity.PayMethod;
import com.example.pillyohae.payment.entity.Payment;
import com.example.pillyohae.payment.entity.PaymentFailLog;
import com.example.pillyohae.payment.entity.TossPaymentsVariables;
import com.example.pillyohae.payment.repository.PaymentFailLogRepostiroy;
import com.example.pillyohae.payment.repository.PaymentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
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
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentFailLogRepostiroy paymentFailLogRepostiroy;

    @Value("${toss.secret-key}")
    private String TOSS_SECRET_KEY;

    JSONParser parser = new JSONParser();
    ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 성공할 경우 주문 저장
     * @param tossResult
     */
    private void success(JSONObject tossResult){
        Payment payment = new Payment(
                (String) tossResult.get(TossPaymentsVariables.MID.getValue()),
                (String) tossResult.get(TossPaymentsVariables.VERSION.getValue()),
                (String) tossResult.get(TossPaymentsVariables.PAYMENTKEY.getValue()),
                (String) tossResult.get(TossPaymentsVariables.STATUS.getValue()),
                (UUID.fromString((String) tossResult.get(TossPaymentsVariables.ORDERID.getValue()))) ,
                (String) tossResult.get(TossPaymentsVariables.ORDERNAME.getValue()),
                (String) tossResult.get(TossPaymentsVariables.REQUESTEDAT.getValue()),
                (String) tossResult.get(TossPaymentsVariables.APPROVEDAT.getValue()),
                (Integer) tossResult.get(TossPaymentsVariables.TOTALAMOUNT.getValue()),
                (Integer) tossResult.get(TossPaymentsVariables.BALANCEAMOUNT.getValue()),
                Enum.valueOf(PayMethod.class,((String) tossResult.get(TossPaymentsVariables.METHOD.getValue()))));
        paymentRepository.save(payment);

        Order paidOrder = orderRepository.findById(payment.getOrderId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        paidOrder.paid();
    }

    /**
     * 실패할 경우 주문 삭제
     * @param tossResult
     */
    private void fail(JSONObject tossResult){
        JSONObject errorObject = (JSONObject) tossResult.get("error");

        PaymentFailLog paymentFailLog = new PaymentFailLog(
                (String)tossResult.get("version"),
                (String)tossResult.get("traceId"),
                (String)errorObject.get("code"),
                (String)errorObject.get("message")
        );

        paymentFailLogRepostiroy.save(paymentFailLog);
        orderRepository.deleteById(UUID.fromString((String)tossResult.get("orderId")));


    }


    @Transactional
    public void requestPayment(JSONObject tossRequest) throws IOException, ParseException {
        HttpURLConnection connection = getTossResult(tossRequest);


        // 결제 승인 요청 후 결과를 받는데 0.668초 정도 소요
        int code = connection.getResponseCode();
        boolean isSuccess = code == 200;
        InputStream responseStream = isSuccess ? connection.getInputStream() : connection.getErrorStream();
        Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8);

        JSONObject tossResult = (JSONObject) parser.parse(reader);
        // 결제 실패 에러 발생 처리
        responseStream.close();

        // 실패시 결제실패 내역 저장 및 order 삭제
        if (!isSuccess) {
            fail(tossResult);
        }
        // 성공시 결제 내역 저장 및 order 상태 변경
        success(tossResult);
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

}
