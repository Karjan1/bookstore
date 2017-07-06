package com.karoljanowski.service;

import com.karoljanowski.domain.*;

import java.util.List;

/**
 * Created by Karol Janowski on 2017-06-27.
 */
public interface CartItemService {
    List<CartItem> findByShopppingCart(ShoppingCart shoppingCart);
    List<CartItem> findByOrder(Order order);
    CartItem updateCartItem(CartItem cartItem);
    CartItem addBookToCartItem(Book book, User user, int qty);
    CartItem findById(Long id);
    void removeById(CartItem cartItem);
    CartItem save(CartItem cartItem);


}
