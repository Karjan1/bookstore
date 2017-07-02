package com.karoljanowski.service;

import com.karoljanowski.domain.UserShipping;

/**
 * Created by Karol Janowski on 2017-06-21.
 */
public interface UserShippingService {
    UserShipping findById(Long id);
    void removeById(Long id);

}
