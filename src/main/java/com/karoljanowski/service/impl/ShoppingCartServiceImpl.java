package com.karoljanowski.service.impl;

import com.karoljanowski.domain.CartItem;
import com.karoljanowski.domain.ShoppingCart;
import com.karoljanowski.repository.ShoppingCartRepository;
import com.karoljanowski.service.CartItemService;
import com.karoljanowski.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Karol Janowski on 2017-06-27.
 */
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private CartItemService cartItemService;

    @Override
    public ShoppingCart updateShoppingCart(ShoppingCart shoppingCart) {
        BigDecimal cartTotal = new BigDecimal(0);
        List<CartItem> cartItemList = cartItemService.findByShopppingCart(shoppingCart);

        for (CartItem cartItem : cartItemList){
            if (cartItem.getBook().getInStockNumber()>0){
                cartItemService.updateCartItem(cartItem);
                cartTotal = cartTotal.add(cartItem.getSubtotal());
            }
        }
        shoppingCart.setGrandTotal(cartTotal);

        shoppingCartRepository.save(shoppingCart);
        return shoppingCart;

    }
}


















