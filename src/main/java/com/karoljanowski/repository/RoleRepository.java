package com.karoljanowski.repository;

import com.karoljanowski.domain.security.Role;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Karol Janowski on 2017-06-20.
 */
public interface RoleRepository extends CrudRepository<Role, Long>{
    Role findByName (String name);
}
