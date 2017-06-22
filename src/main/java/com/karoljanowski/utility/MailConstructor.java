package com.karoljanowski.utility;

import com.karoljanowski.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Created by Karol Janowski on 2017-06-21.
 */
@Component
public class MailConstructor {

    @Autowired
    Environment env;

    public SimpleMailMessage constructResetTokenEmail(String contextPath, Locale locale, String token, User user, String password){
        String url = contextPath + "/newUser?token=" + token;
        String message = "\n Please click on this to verify your email. Your password is:"+ "\n" + password;
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(user.getEmail());
        email.setSubject("The Bookstore confirmation message.");
        email.setText(url+message);
        email.setFrom(env.getProperty("support.email"));
        return email;
    }
}



















