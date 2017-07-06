package com.karoljanowski.repository;

import com.karoljanowski.domain.CartItem;
import com.karoljanowski.domain.Order;
import com.karoljanowski.domain.ShoppingCart;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Karol Janowski on 2017-06-20.
 */
@Transactional
public interface CartItemRepository extends CrudRepository<CartItem, Long>{
    List<CartItem> findByShoppingCart(ShoppingCart shoppingCart);
    List<CartItem> findByOrder(Order order);
}
