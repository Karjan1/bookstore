package com.karoljanowski.service;

import com.karoljanowski.domain.Payment;
import com.karoljanowski.domain.UserPayment;

/**
 * Created by Karol Janowski on 2017-06-21.
 */
public interface PaymentService {
    Payment setByUserPayment(UserPayment userPayment, Payment payment);

}
