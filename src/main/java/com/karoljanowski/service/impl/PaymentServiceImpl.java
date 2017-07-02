package com.karoljanowski.service.impl;

import com.karoljanowski.domain.Payment;
import com.karoljanowski.domain.UserPayment;
import com.karoljanowski.service.PaymentService;
import org.springframework.stereotype.Service;

/**
 * Created by Karol Janowski on 2017-06-29.
 */
@Service
public class PaymentServiceImpl implements PaymentService{

    @Override
    public Payment setByUserPayment(UserPayment userPayment, Payment payment) {
        payment.setCardNumber(userPayment.getCardNumber());
        payment.setCvc(userPayment.getCvc());
        payment.setExpiryMonth(userPayment.getExpiryMonth());
        payment.setExpiryYear(userPayment.getExpiryYear());
        payment.setHolderName(userPayment.getHolderName());
        payment.setType(userPayment.getType());
        return payment;
    }
}

















