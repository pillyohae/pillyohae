package com.example.pillyohae.payment.controller;


import com.example.pillyohae.payment.dto.PaymentSuccessDto;
import com.example.pillyohae.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@RequestMapping("/toss")
@RequiredArgsConstructor
public class TossController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final PaymentService paymentService;



    @RequestMapping(value = "/confirm")
    public ResponseEntity<JSONObject> confirmPayment(@RequestBody String jsonBody) throws Exception {

        return paymentService.pay(jsonBody);
    }




}