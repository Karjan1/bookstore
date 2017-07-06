package com.karoljanowski.service.impl;

import com.karoljanowski.domain.*;
import com.karoljanowski.domain.security.PasswordResetToken;
import com.karoljanowski.domain.security.UserRole;
import com.karoljanowski.repository.*;
import com.karoljanowski.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Karol Janowski on 2017-06-21.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserPaymentRepository userPaymentRepository;

    @Autowired
    private UserShippingRepository userShippingRepository;

    @Override
    public PasswordResetToken getPasswordResetToken(String token) {
        return passwordResetTokenRepository.findByToken(token);
    }

    @Override
    public void createPasswordResetTokenForUser(User user, String token) {
        final PasswordResetToken myToken = new PasswordResetToken(token, user);
        passwordResetTokenRepository.save(myToken);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public User createUser(User user, Set<UserRole> userRoles) throws Exception {
        User localUser = userRepository.findByUsername(user.getUsername());

        if (localUser != null){
            throw new Exception("User exists. Nothing will be done");
        } else {
            for (UserRole ur : userRoles) {
                roleRepository.save(ur.getRole());
            }
            user.getUserRoles().addAll(userRoles);

            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setUser(user);
            user.setShoppingCart(shoppingCart);

            user.setUserShippingList(new ArrayList<UserShipping>());
            user.setUserPaymentList(new ArrayList<UserPayment>());

            localUser = userRepository.save(user);
        }
        return localUser;
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public void updateUserBilling(UserBilling userBilling, UserPayment userPayment, User user) {
        userPayment.setUser(user);
        userPayment.setUserBilling(userBilling);
        setUserDefaultPayment(userPayment.getId(),user);
        userBilling.setUserPayment(userPayment);
        user.getUserPaymentList().add(userPayment);
        save(user);
    }

    @Override
    public void setUserDefaultPayment(Long defaultPaymentId, User user) {
        List<UserPayment> userPaymentList = (List<UserPayment>) userPaymentRepository.findAll();
        for (UserPayment userPayment : userPaymentList){
            if (userPayment.isDefaultPayment()){
                userPayment.setDefaultPayment(false);
                userPaymentRepository.save(userPayment);
            }
            if (userPayment.getId()==defaultPaymentId){
                userPayment.setDefaultPayment(true);
                userPaymentRepository.save(userPayment);
            }
        }

    }

    @Override
    public void setUserDefaultShipping(Long defaultShippingId, User user) {
        List<UserShipping> userShippingList = (List<UserShipping>) userShippingRepository.findAll();
        for (UserShipping userShipping : userShippingList){
            if (userShipping.isUserShippingDefault()){
                userShipping.setUserShippingDefault(false);
                userShippingRepository.save(userShipping);
            }
            if (userShipping.getId()==defaultShippingId){
                userShipping.setUserShippingDefault(true);
                userShippingRepository.save(userShipping);
            }
        }

    }

    @Override
    public void updateUserShipping(UserShipping userShipping, User user) {
        userShipping.setUser(user);
        List<UserShipping> userShippingList = user.getUserShippingList();
        userShippingList.add(userShipping);
//        for (UserShipping temp : userShippingList){
//            if (temp.getId()==userShipping.getId()){
//                userShipping.setUserShippingDefault(true);
//                userShippingRepository.save(userShipping);
//            } else {
//                temp.setUserShippingDefault(false);
//                userShippingRepository.save(temp);
//            }
//        }
        save(user);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findOne(id);
    }
}



















