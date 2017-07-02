package com.karoljanowski.repository;

import com.karoljanowski.domain.BookToCartItem;
import com.karoljanowski.domain.CartItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Karol Janowski on 2017-06-20.
 */
@Transactional
public interface BookToCartItemRepository extends CrudRepository<BookToCartItem, Long>{
    void deleteByCartItem(CartItem cartItem);
}
