package com.karoljanowski.repository;

import com.karoljanowski.domain.ShoppingCart;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Karol Janowski on 2017-06-20.
 */
public interface ShoppingCartRepository extends CrudRepository<ShoppingCart, Long>{
}
