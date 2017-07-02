package com.karoljanowski.repository;

import com.karoljanowski.domain.UserPayment;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Karol Janowski on 2017-06-20.
 */
public interface UserPaymentRepository extends CrudRepository<UserPayment, Long>{
}
