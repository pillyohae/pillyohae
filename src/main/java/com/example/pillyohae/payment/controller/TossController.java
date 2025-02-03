package com.example.pillyohae.payment.controller;


import com.example.pillyohae.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * TossPayments 결제 요청을 처리하는 컨트롤러
 * - 클라이언트에서 전달한 결제 정보를 받아서 결제 진행
 * - 결제가 성공하면 결제 완료 정보를 반환
 */
@RestController
@RequestMapping("/toss")
@RequiredArgsConstructor
public class TossController {

    private final PaymentService paymentService;

    /**
     * 결제 승인 요청
     *
     * @param jsonBody 클라이언트에서 전달한 결제 요청 데이터 (JSON)
     * @return 결제 승인 결과 (JSON)
     */
    @PostMapping(value = "/confirm")
    public ResponseEntity<JSONObject> confirmPayment(@RequestBody String jsonBody) {
        return ResponseEntity.ok().body(paymentService.pay(jsonBody));
    }
}
