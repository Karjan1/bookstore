package com.karoljanowski.service.impl;

import com.karoljanowski.domain.UserPayment;
import com.karoljanowski.repository.UserPaymentRepository;
import com.karoljanowski.service.UserPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Karol Janowski on 2017-06-29.
 */
@Service
public class UserPaymentServiceImpl implements UserPaymentService{

    @Autowired
    private UserPaymentRepository userPaymentRepository;

    @Override
    public UserPayment findById(Long id) {
        return userPaymentRepository.findOne(id);
    }

    @Override
    public void removeById(Long id) {
        userPaymentRepository.delete(id);
    }
}

















