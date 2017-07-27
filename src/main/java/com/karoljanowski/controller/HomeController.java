package com.karoljanowski.controller;

import com.karoljanowski.domain.*;
import com.karoljanowski.domain.security.PasswordResetToken;
import com.karoljanowski.domain.security.Role;
import com.karoljanowski.domain.security.UserRole;
import com.karoljanowski.service.*;
import com.karoljanowski.service.impl.UserSecurityService;
import com.karoljanowski.utility.MailConstructor;
import com.karoljanowski.utility.SecurityUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;
import java.security.Principal;
import java.util.*;

/**
 * Created by Karol Janowski on 2017-06-19.
 */
@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private MailConstructor mailConstructor;

    @Autowired
    private UserSecurityService userSecurityService;

    @Autowired
    private SecurityUtility securityUtility;

    @Autowired
    private BookService bookService;

    @Autowired
    private UserPaymentService userPaymentService;

    @Autowired
    private UserShippingService userShippingService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartItemService cartItemService;

    @RequestMapping("/")
    public String index(){
        return "index";
    }

    @RequestMapping("/badRequestPage")
    public String badRequestPage(){
        return "badRequestPage";
    }

    @RequestMapping(value = "/newUser", method = RequestMethod.POST)
    public String newUserPost(HttpServletRequest request, @ModelAttribute("email") String email,
                              @ModelAttribute("username") String username, Model model) throws Exception{
        model.addAttribute("classActiveNewAccount", true);
        model.addAttribute("username", username);
        model.addAttribute("email", email);

        if (userService.findByUsername(username) != null){
            model.addAttribute("usernameExists", true);
            return "myAccount";
        }

        if (userService.findByEmail(email) != null){
            model.addAttribute("emailExists", true);
            return "myAccount";
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);

        String password = securityUtility.randomPassword();
        String encryptedPassword = securityUtility.passwordEncoder().encode(password);
        user.setPassword(encryptedPassword);

        Role role = new Role();
        role.setRoleId(1);
        role.setName("ROLE_USER");
        Set<UserRole> userRoles = new HashSet<>();
        userRoles.add(new UserRole(user, role));
        userService.createUser(user, userRoles);

        String token = UUID.randomUUID().toString();
        userService.createPasswordResetTokenForUser(user, token);

        String appUrl = "http//:" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        SimpleMailMessage simpleMailMessage = mailConstructor.constructResetTokenEmail(appUrl, request.getLocale(), token, user, password);
        mailSender.send(simpleMailMessage);

        model.addAttribute("emailSent", true);


        return "myAccount";
    }

    @RequestMapping("/newUser")
    public String newUser(Model model, Locale locale, @RequestParam("token") String token){
        PasswordResetToken passToken = userService.getPasswordResetToken(token);

        if(passToken == null){
            String message = "Invalid token";
            model.addAttribute("message", message);
            return "redirect:badRequest";
        }

        User user = passToken.getUser();
        String username = user.getUsername();
        UserDetails userDetails = userSecurityService.loadUserByUsername(username);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        model.addAttribute("user", user);
        model.addAttribute("classActiveEdit", true);

        return "myProfile";
    }

    @RequestMapping("/login")
    public String login(Model model){
        model.addAttribute("classActiveLogin", true);
        return "myAccount";
    }

    @RequestMapping("/forgetPassword")
    public String forgetPassword(HttpServletRequest request, @ModelAttribute("email") String email,
                                  Model model){
        model.addAttribute("classActiveForgetPassword", true);

        User user = userService.findByEmail(email);

        if (user == null){
            model.addAttribute("emailNotExist", true);
            return "myAccount";
        }

        String password = securityUtility.randomPassword();
        String encryptedPassword = securityUtility.passwordEncoder().encode(password);
        user.setPassword(encryptedPassword);


        userService.save(user);

        String token = UUID.randomUUID().toString();
        userService.createPasswordResetTokenForUser(user, token);

        String appUrl = "http//:" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        SimpleMailMessage simpleMailMessage = mailConstructor.constructResetTokenEmail(appUrl, request.getLocale(), token, user, password);
        mailSender.send(simpleMailMessage);

        model.addAttribute("forgetPasswordEmailSent", true);
        return "myAccount";
    }

    @RequestMapping("/bookshelf")
    public String bookshelf(Model model, Principal principal){
        List<Book> bookList = bookService.findAll();
        if (principal != null){
            User user = userService.findByUsername(principal.getName());
            model.addAttribute("user", user);
        }

        model.addAttribute("bookList", bookList);

        return "bookshelf";
    }

    @RequestMapping("/bookDetail")
    public String bookDetail(@PathParam("id") Long id, Model model, Principal principal){
        if (principal != null){
            User user = userService.findByUsername(principal.getName());
            model.addAttribute("user", user);
        }

        Book book = bookService.findOne(id);
        model.addAttribute("book", book);
        List<Integer> qtyList = Arrays.asList(1,2,3,4,5,6,7,8,9,10);
        model.addAttribute("qtyList", qtyList);
        model.addAttribute("qty", 1);

        return "bookDetail";
    }

    @RequestMapping("/myProfile")
    public String myProfile(Model model, Principal principal){
        User user= userService.findByUsername(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("userPaymentList", user.getUserPaymentList());
        model.addAttribute("userShippingList", user.getUserShippingList());
        model.addAttribute("orderList", user.getOrderList());

        UserShipping userShipping = new UserShipping();
        model.addAttribute("userShipping", userShipping);

        model.addAttribute("listOfCreditCards", true);
        model.addAttribute("listOfShippingAddresses", true);

        model.addAttribute("classActiveEdit", true);

        return "myProfile";
    }

    @RequestMapping("/listOfCreditCards")
    public String listOfCreditCards(Model model, Principal principal, HttpServletRequest request){

        User user= userService.findByUsername(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("userPaymentList", user.getUserPaymentList());
        model.addAttribute("userShippingList", user.getUserShippingList());
        model.addAttribute("orderList", user.getOrderList());

        model.addAttribute("listOfCreditCards", true);
        model.addAttribute("listOfShippingAddresses", true);
        model.addAttribute("classActiveBilling", true);


        return "myProfile";
    }

    @RequestMapping("/listOfShippingAddresses")
    public String listOfShippingAddresses(Model model, Principal principal, HttpServletRequest request){

        User user= userService.findByUsername(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("userPaymentList", user.getUserPaymentList());
        model.addAttribute("userShippingList", user.getUserShippingList());
        model.addAttribute("orderList", user.getOrderList());

        model.addAttribute("listOfCreditCards", true);
        model.addAttribute("listOfShippingAddresses", true);
        model.addAttribute("classActiveBilling", true);

        return "myProfile";
    }



    @RequestMapping("/addNewCreditCard")
    public String addNewCreditCard(Model model, Principal principal){

        User user= userService.findByUsername(principal.getName());
        model.addAttribute("user", user);

        model.addAttribute("addNewCreditCard", true);
        model.addAttribute("classActiveBilling", true);
        model.addAttribute("listOfShippingAddresses", true);
        model.addAttribute("orderList", user.getOrderList());



        UserBilling userBilling =new UserBilling();
        UserPayment userPayment = new UserPayment();

        model.addAttribute("userBilling", userBilling);
        model.addAttribute("userPayment", userPayment);

        return "myProfile";
    }

    @RequestMapping(value = "/addNewCreditCard", method = RequestMethod.POST)
    public String addNewCreditCardPost(Model model, Principal principal, @ModelAttribute("userPayment") UserPayment userPayment,@ModelAttribute("userBilling") UserBilling userBilling) {

        User user = userService.findByUsername(principal.getName());
        userService.updateUserBilling(userBilling, userPayment, user);

        model.addAttribute("user", user);
        model.addAttribute("userPaymentList", user.getUserPaymentList());
        model.addAttribute("userShippingList", user.getUserShippingList());
        model.addAttribute("orderList", user.getOrderList());

        model.addAttribute("classActiveBilling", true);
        model.addAttribute("listOfShippingAddresses", true);
        model.addAttribute("listOfCreditCards", true);


        return "myProfile";
    }
    @RequestMapping("/addNewShippingAddress")
    public String addNewShippingAddress(Model model, Principal principal){

        User user= userService.findByUsername(principal.getName());
        model.addAttribute("user", user);

        model.addAttribute("addNewShippingAddress", true);
        model.addAttribute("classActiveShipping", true);
        model.addAttribute("orderList", user.getOrderList());
        model.addAttribute("listOfCreditCards", true);




        UserShipping userShipping = new UserShipping();

        model.addAttribute("userShipping", userShipping);

        return "myProfile";
    }

    @RequestMapping("/updateShippingAddress")
    public String updateShippingAddress(@RequestParam("id") Long userShippingId, Model model, Principal principal){
        User user = userService.findByUsername(principal.getName());
        UserShipping userShipping = userShippingService.findById(userShippingId);

        if (user.getId()!=userShipping.getUser().getId()){
            return "badRequestPage";
        } else {
            model.addAttribute("user", user);
            model.addAttribute("userShipping", userShipping);
            model.addAttribute("userPaymentList", user.getUserPaymentList());
            model.addAttribute("userShippingList", user.getUserShippingList());

            model.addAttribute("classActiveShipping", true);
            model.addAttribute("addNewShippingAddress", true);
            model.addAttribute("listOfCreditCards", true);

        }

        return "myProfile";
    }

    @RequestMapping(value = "/addNewShippingAddress", method = RequestMethod.POST)
    public String addNewShippingAddressPost(Model model, Principal principal, @ModelAttribute("userShipping") UserShipping userShipping) {

        User user = userService.findByUsername(principal.getName());
        userService.updateUserShipping(userShipping, user);

        model.addAttribute("user", user);
        model.addAttribute("userPaymentList", user.getUserPaymentList());
        model.addAttribute("userShippingList", user.getUserShippingList());
        model.addAttribute("orderList", user.getOrderList());

        model.addAttribute("classActiveShipping", true);
        model.addAttribute("listOfShippingAddresses", true);
        model.addAttribute("listOfCreditCards", true);

        return "myProfile";
    }

    @RequestMapping("/updateCreditCard")
    public String updateCreditCard(@RequestParam("id") Long creditCardId, Model model, Principal principal){
            User user = userService.findByUsername(principal.getName());
            UserPayment userPayment = userPaymentService.findById(creditCardId);

            if (user.getId()!=userPayment.getUser().getId()){
                return "badRequestPage";
            } else {
                model.addAttribute("user", user);
                UserBilling userBilling = userPayment.getUserBilling();
                model.addAttribute("userBilling", userBilling);
                model.addAttribute("userPayment", userPayment);
                model.addAttribute("userPaymentList", user.getUserPaymentList());
                model.addAttribute("userShippingList", user.getUserShippingList());
                model.addAttribute("orderList", user.getOrderList());


                model.addAttribute("classActiveBilling", true);
                model.addAttribute("listOfShippingAddresses", true);
                model.addAttribute("addNewCreditCard", true);
            }

        return "myProfile";
    }

    @RequestMapping("/deleteCreditCard")
    public String deleteCreditCard(@RequestParam("id") Long creditCardId, Model model, Principal principal){
        User user = userService.findByUsername(principal.getName());
        UserPayment userPayment = userPaymentService.findById(creditCardId);

        if (user.getId()!=userPayment.getUser().getId()){
            return "badRequestPage";
        } else {
            model.addAttribute("user", user);
            userPaymentService.removeById(creditCardId);

            model.addAttribute("userPaymentList", user.getUserPaymentList());
            model.addAttribute("userShippingList", user.getUserShippingList());
            model.addAttribute("orderList", user.getOrderList());


            model.addAttribute("classActiveBilling", true);
            model.addAttribute("listOfShippingAddresses", true);
            model.addAttribute("listOfCreditCards", true);
        }

        return "myProfile";
    }

    @RequestMapping("/deleteShippingAddress")
    public String deleteShippingAddress(@RequestParam("id") Long shippingAddressId, Model model, Principal principal){
        User user = userService.findByUsername(principal.getName());
        UserShipping userShipping = userShippingService.findById(shippingAddressId);

        if (user.getId()!=userShipping.getUser().getId()){
            return "badRequestPage";
        } else {
            model.addAttribute("user", user);
            userShippingService.removeById(shippingAddressId);

            model.addAttribute("userPaymentList", user.getUserPaymentList());
            model.addAttribute("userShippingList", user.getUserShippingList());
            model.addAttribute("orderList", user.getOrderList());


            model.addAttribute("classActiveShipping", true);
            model.addAttribute("listOfShippingAddresses", true);
            model.addAttribute("listOfCreditCards", true);
        }

        return "myProfile";
    }

    @RequestMapping(value = "/setDefaultPayment", method = RequestMethod.POST)
    public String setDefaultPayment(@ModelAttribute("defaultUserPaymentId") Long defaultPaymentId, Model model, Principal principal){
        User user = userService.findByUsername(principal.getName());
        userService.setUserDefaultPayment(defaultPaymentId, user);

            model.addAttribute("user", user);

            model.addAttribute("userPaymentList", user.getUserPaymentList());
            model.addAttribute("userShippingList", user.getUserShippingList());
        model.addAttribute("orderList", user.getOrderList());


        model.addAttribute("classActiveBilling", true);
            model.addAttribute("listOfShippingAddresses", true);
            model.addAttribute("listOfCreditCards", true);


        return "myProfile";
    }


    @RequestMapping(value = "/setDefaultShippingAddress", method = RequestMethod.POST)
    public String setDefaultShippingAddressPost(@ModelAttribute("defaultUserShippingAddressId") Long defaultShippingId, Model model, Principal principal){
        User user = userService.findByUsername(principal.getName());
        userService.setUserDefaultShipping(defaultShippingId, user);

        model.addAttribute("user", user);

        model.addAttribute("userPaymentList", user.getUserPaymentList());
        model.addAttribute("userShippingList", user.getUserShippingList());
        model.addAttribute("orderList", user.getOrderList());


        model.addAttribute("classActiveShipping", true);
        model.addAttribute("listOfShippingAddresses", true);
        model.addAttribute("listOfCreditCards", true);


        return "myProfile";
    }

    @RequestMapping(value = "/updateUserInfo", method = RequestMethod.POST)
    public String updateUserInfoPost(Model model, @ModelAttribute("user") User user, @ModelAttribute("newPassword") String newPassword) throws Exception {

        User currentUser=userService.findById(user.getId());
        if (currentUser==null){
            throw new Exception("User not found");
        }

        //CHECK EMAIL N USERNAME EXISTS???

        if (newPassword!=null && newPassword.length()>=1){
            BCryptPasswordEncoder passwordEncoder = SecurityUtility.passwordEncoder();
            String dbPassword = currentUser.getPassword();
            if (passwordEncoder.matches(user.getPassword(), dbPassword)){
                currentUser.setPassword(passwordEncoder.encode(newPassword));
            } else {
                model.addAttribute("incorrectPassword", true);
                model.addAttribute("classActiveEdit", true);
                return "myProfile";
            }
            }

            currentUser.setFirstName(user.getFirstName());
            currentUser.setLastName(user.getLastName());
            currentUser.setUsername(user.getUsername());
            currentUser.setEmail(user.getEmail());

            userService.save(currentUser);
        model.addAttribute("updateSuccess", true);
        model.addAttribute("classActiveEdit", true);
        model.addAttribute("user", currentUser);

        model.addAttribute("orderList", user.getOrderList());
        model.addAttribute("listOfCreditCards", true);
        model.addAttribute("listOfShippingAddresses", true);

        String username = currentUser.getUsername();
        UserDetails userDetails = userSecurityService.loadUserByUsername(username);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);


        return "myProfile";
    }

    @RequestMapping("/orderDetail")
    public String orderDetail(@RequestParam("id") Long orderId, Model model, Principal principal){
        User user = userService.findByUsername(principal.getName());
        Order order =orderService.findOne(orderId);

        if (user.getId()!=order.getUser().getId()){
            return "badRequestPage";
        } else {
            List<CartItem> cartItemList= cartItemService.findByOrder(order);

            model.addAttribute("user", user);
            model.addAttribute("order", order);
            model.addAttribute("cartItemList", cartItemList);

            model.addAttribute("userPaymentList", user.getUserPaymentList());
            model.addAttribute("userShippingList", user.getUserShippingList());
            model.addAttribute("orderList", user.getOrderList());

            model.addAttribute("classActiveOrder", true);
            model.addAttribute("listOfCreditCards", true);
            model.addAttribute("displayOrderDetail", true);
            model.addAttribute("listOfShippingAddresses", true);
        }

        return "myProfile";
    }

}



















