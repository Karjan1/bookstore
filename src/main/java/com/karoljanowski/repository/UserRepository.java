package com.karoljanowski.repository;

import com.karoljanowski.domain.User;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Karol Janowski on 2017-06-20.
 */
public interface UserRepository extends CrudRepository<User, Long>{
    User findByUsername (String username);
    User findByEmail (String email);
}
