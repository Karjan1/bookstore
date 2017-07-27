package com.karoljanowski.service.impl;

import com.karoljanowski.domain.User;
import com.karoljanowski.domain.security.UserRole;
import com.karoljanowski.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;


/**
 * Created by Karol Janowski on 2017-06-08.
 */
@Service
public class UserSecurityService implements UserDetailsService {

    private static final Logger LOG = LoggerFactory.getLogger(UserSecurityService.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user =userRepository.findByUsername(username);
        if (null == user){
            LOG.warn("Username {} not found", username);
            throw new UsernameNotFoundException("Username " + username + " not found");
        }
        Set<UserRole> roleList = new HashSet<>();
        roleList =user.getUserRoles();
        for (UserRole role : roleList){
            if (role.getRole().getName().equalsIgnoreCase("ROLE_USER")){
               return user;
            }
        }
        LOG.warn("Unauthorized access, {} does not have access.", username);
        throw new UsernameNotFoundException("Access denied to " + username);

    }
}












