package com.karoljanowski.service;

import com.karoljanowski.domain.UserPayment;

/**
 * Created by Karol Janowski on 2017-06-21.
 */
public interface UserPaymentService {
    UserPayment findById(Long id);
    void removeById(Long id);

}
