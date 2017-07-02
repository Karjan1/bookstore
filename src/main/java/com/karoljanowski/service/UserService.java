package com.karoljanowski.service;

import com.karoljanowski.domain.User;
import com.karoljanowski.domain.UserBilling;
import com.karoljanowski.domain.UserPayment;
import com.karoljanowski.domain.UserShipping;
import com.karoljanowski.domain.security.PasswordResetToken;
import com.karoljanowski.domain.security.UserRole;

import java.util.Set;

/**
 * Created by Karol Janowski on 2017-06-21.
 */
public interface UserService {
    PasswordResetToken getPasswordResetToken(final String token);
    void createPasswordResetTokenForUser(final User user, final String token);
    User findByUsername(String username);
    User findByEmail(String email);
    User createUser(User user, Set<UserRole> userRoles) throws Exception;
    User save(User user);
    void updateUserBilling(UserBilling userBilling, UserPayment userPayment, User user);
    void setUserDefaultPayment(Long defaultPaymentId, User user);
    void setUserDefaultShipping(Long defaultShippingId, User user);
    void  updateUserShipping(UserShipping userShipping, User user);

}
