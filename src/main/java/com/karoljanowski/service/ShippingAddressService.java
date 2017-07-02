package com.karoljanowski.service;

import com.karoljanowski.domain.ShippingAddress;
import com.karoljanowski.domain.UserShipping;

/**
 * Created by Karol Janowski on 2017-06-21.
 */
public interface ShippingAddressService {
    ShippingAddress setByUserShipping(UserShipping userShipping, ShippingAddress shippingAddress);

}
