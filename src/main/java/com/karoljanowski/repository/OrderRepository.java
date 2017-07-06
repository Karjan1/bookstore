package com.karoljanowski.repository;

import com.karoljanowski.domain.Order;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Karol Janowski on 2017-07-04.
 */
public interface OrderRepository extends CrudRepository<Order, Long> {
}
