package com.example.pillyohae.global.message_queue.service;

import com.example.pillyohae.order.repository.OrderRepository;
import com.example.pillyohae.order.service.OrderService;
import com.example.pillyohae.payment.entity.PayMethod;
import com.example.pillyohae.payment.entity.Payment;
import com.example.pillyohae.payment.entity.TossPaymentsVariables;
import com.example.pillyohae.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final PaymentRepository paymentRepository;
    private final OrderService orderService;

    @Transactional
    public void savePayment(JSONObject tossResult) throws IOException, ParseException {
        Payment payment = new Payment(
                (String) tossResult.get(TossPaymentsVariables.MID.getValue()),
                (String) tossResult.get(TossPaymentsVariables.VERSION.getValue()),
                (String) tossResult.get(TossPaymentsVariables.PAYMENTKEY.getValue()),
                (String) tossResult.get(TossPaymentsVariables.STATUS.getValue()),
                (UUID.fromString((String) tossResult.get(TossPaymentsVariables.ORDERID.getValue()))) ,
                (String) tossResult.get(TossPaymentsVariables.ORDERNAME.getValue()),
                (String) tossResult.get(TossPaymentsVariables.REQUESTEDAT.getValue()),
                (String) tossResult.get(TossPaymentsVariables.APPROVEDAT.getValue()),
                (Long) tossResult.get(TossPaymentsVariables.TOTALAMOUNT.getValue()),
                (Long) tossResult.get(TossPaymentsVariables.BALANCEAMOUNT.getValue()),
                Enum.valueOf(PayMethod.class,((String) tossResult.get(TossPaymentsVariables.METHOD.getValue()))));
        paymentRepository.save(payment);

        orderService.updateOrderPaid((UUID.fromString((String)tossResult.get(TossPaymentsVariables.ORDERID.getValue()))));

    }
}