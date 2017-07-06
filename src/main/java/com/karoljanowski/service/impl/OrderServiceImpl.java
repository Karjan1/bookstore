package com.karoljanowski.service.impl;

import com.karoljanowski.domain.*;
import com.karoljanowski.repository.OrderRepository;
import com.karoljanowski.service.CartItemService;
import com.karoljanowski.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Karol Janowski on 2017-07-04.
 */
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private CartItemService cartItemService;

    @Autowired
    private OrderRepository orderRepository;


    //synchronized. Thanks to synchronized this method will be executed only by one user at once
    @Override
    public synchronized Order createOrder(ShoppingCart shoppingCart, ShippingAddress shippingAddress, Billing billing, Payment payment, String shippingMethod, User user) {
        Order order = new Order();
        order.setBilling(billing);
        order.setOrderStatus("created");
        order.setPayment(payment);
        order.setShippingAddress(shippingAddress);
        order.setShippingMethod(shippingMethod);

        List<CartItem> cartItemList = cartItemService.findByShopppingCart(shoppingCart);

        for (CartItem cartItem:cartItemList){
            Book book = cartItem.getBook();
            cartItem.setOrder(order);
            book.setInStockNumber(book.getInStockNumber()-cartItem.getQty());
        }

        order.setUser(user);
        order.setCartItemList(cartItemList);
        order.setOrderDate(Calendar.getInstance().getTime());
        order.setOrderTotal(shoppingCart.getGrandTotal());
        shippingAddress.setOrder(order);
        billing.setOrder(order);
        payment.setOrder(order);

        order = orderRepository.save(order);

        return order;
    }

    @Override
    public Order findOne(Long id) {
        return orderRepository.findOne(id);
    }
}

















