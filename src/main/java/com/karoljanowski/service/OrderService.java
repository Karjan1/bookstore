package com.karoljanowski.service;

import com.karoljanowski.domain.*;

/**
 * Created by Karol Janowski on 2017-07-04.
 */
public interface OrderService {

    Order findOne(Long id);
    Order createOrder(ShoppingCart shoppingCart, ShippingAddress shippingAddress, Billing billing, Payment payment, String shippingMethod, User user);
}
