package com.example.pillyohae.domain.payment.controller;


import com.example.pillyohae.domain.payment.dto.PaymentDataDto;
import com.example.pillyohae.domain.payment.entity.PayMethod;
import com.example.pillyohae.domain.payment.entity.TossPaymentsVariables;
import com.example.pillyohae.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URL;


@Controller
@RequestMapping("/toss")
@RequiredArgsConstructor
public class WidgetController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final PaymentService paymentService;
    @Value("${toss.secret-key}")
    private String TOSS_SECRET_KEY;


    @GetMapping("/checkout")
    public String checkoutPage() {
        return "toss/checkout"; // templates/toss/checkout.html 파일을 반환
    }

    @GetMapping("/success")
    public String successPage() {
        return "success"; // templates/toss/checkout.html 파일을 반환
    }

    @GetMapping("/fail")
    public String failPage() {
        return "fail"; // templates/toss/checkout.html 파일을 반환
    }


    @RequestMapping(value = "/confirm")
    public ResponseEntity<JSONObject> confirmPayment(@RequestBody String jsonBody) throws Exception {
        System.out.println(TOSS_SECRET_KEY);
        JSONParser parser = new JSONParser();
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

        // 토스페이먼츠 API는 시크릿 키를 사용자 ID로 사용하고, 비밀번호는 사용하지 않습니다.
        // 비밀번호가 없다는 것을 알리기 위해 시크릿 키 뒤에 콜론을 추가합니다.
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
        outputStream.write(obj.toString().getBytes("UTF-8"));

        int code = connection.getResponseCode();
        boolean isSuccess = code == 200;

        InputStream responseStream = isSuccess ? connection.getInputStream() : connection.getErrorStream();

        // 결제 성공 및 실패 비즈니스 로직을 구현하세요.
        Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8);
        JSONObject jsonObject = (JSONObject) parser.parse(reader);
        responseStream.close();
        System.out.println(jsonObject.toJSONString());
        paymentService.savePayment(getPaymentDataDto(jsonObject));
        return ResponseEntity.status(code).body(jsonObject);
    }



    private PaymentDataDto getPaymentDataDto(JSONObject jsonObject) {
        return PaymentDataDto.builder()
                .mid((String) jsonObject.get(TossPaymentsVariables.MID.getValue()))                  // 가맹점 ID
                .version((String) jsonObject.get(TossPaymentsVariables.VERSION.getValue()))          // API 버전
                .paymentKey((String) jsonObject.get(TossPaymentsVariables.PAYMENTKEY.getValue()))    // 결제 고유 키
                .status((String) jsonObject.get(TossPaymentsVariables.STATUS.getValue()))            // 결제 상태
                .method(Enum.valueOf(PayMethod.class,((String) jsonObject.get(TossPaymentsVariables.METHOD.getValue()))))            // 결제 수단
                .orderId((String) jsonObject.get(TossPaymentsVariables.ORDERID.getValue()))          // 주문 ID
                .orderName((String) jsonObject.get(TossPaymentsVariables.ORDERNAME.getValue()))      // 주문 이름
                .requestedAt((String) jsonObject.get(TossPaymentsVariables.REQUESTEDAT.getValue()))  // 결제 요청 시간
                .approvedAt((String) jsonObject.get(TossPaymentsVariables.APPROVEDAT.getValue()))    // 결제 승인 시간
                .totalAmount((Long) jsonObject.get(TossPaymentsVariables.TOTALAMOUNT.getValue()))  // 총 결제 금액
                .balanceAmount((Long) jsonObject.get(TossPaymentsVariables.BALANCEAMOUNT.getValue())) // 잔액 금액
                .build();
    }
}