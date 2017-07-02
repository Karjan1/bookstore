package com.karoljanowski.service;

import com.karoljanowski.domain.Book;
import com.karoljanowski.domain.CartItem;
import com.karoljanowski.domain.ShoppingCart;
import com.karoljanowski.domain.User;

import java.util.List;

/**
 * Created by Karol Janowski on 2017-06-27.
 */
public interface CartItemService {
    List<CartItem> findByShopppingCart(ShoppingCart shoppingCart);
    CartItem updateCartItem(CartItem cartItem);
    CartItem addBookToCartItem(Book book, User user, int qty);
    CartItem findById(Long id);
    void removeById(CartItem cartItem);
}
