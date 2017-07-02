package com.karoljanowski.service.impl;

import com.karoljanowski.domain.UserShipping;
import com.karoljanowski.repository.UserShippingRepository;
import com.karoljanowski.service.UserShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Karol Janowski on 2017-06-29.
 */
@Service
public class UserShippingServiceImpl implements UserShippingService{

    @Autowired
    private UserShippingRepository userShippingRepository;

    @Override
    public UserShipping findById(Long id) {
        return userShippingRepository.findOne(id);
    }

    @Override
    public void removeById(Long id) {
        userShippingRepository.delete(id);
    }
}

















