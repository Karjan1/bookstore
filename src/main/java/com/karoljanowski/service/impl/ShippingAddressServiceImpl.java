package com.karoljanowski.service.impl;

import com.karoljanowski.domain.ShippingAddress;
import com.karoljanowski.domain.UserShipping;
import com.karoljanowski.service.ShippingAddressService;
import org.springframework.stereotype.Service;

/**
 * Created by Karol Janowski on 2017-06-29.
 */
@Service
public class ShippingAddressServiceImpl implements ShippingAddressService{

    @Override
    public ShippingAddress setByUserShipping(UserShipping userShipping, ShippingAddress shippingAddress) {
        shippingAddress.setShippingAddressName(userShipping.getUserShippingName());
        shippingAddress.setShippingAddressStreet1(userShipping.getUserShippingStreet1());
        shippingAddress.setShippingAddressStreet2(userShipping.getUserShippingStreet2());
        shippingAddress.setShippingAddressCity(userShipping.getUserShippingCity());
        shippingAddress.setShippingAddressZipCode(userShipping.getUserShippingZipCode());
        shippingAddress.setShippingAddressCountry(userShipping.getUserShippingCountry());
        return shippingAddress;
    }
}

















