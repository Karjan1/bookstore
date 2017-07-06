package com.karoljanowski.controller;

import com.karoljanowski.domain.*;
import com.karoljanowski.service.*;
import com.karoljanowski.utility.MailConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

/**
 * Created by Karol Janowski on 2017-06-30.
 */
@Controller
public class CheckoutController {

    private ShippingAddress  shippingAddress = new ShippingAddress();
    private Billing billing = new Billing();
    private Payment payment = new Payment();

    @Autowired
    private UserService userService;

    @Autowired
    private CartItemService cartItemService;

    @Autowired
    private ShippingAddressService shippingAddressService;

    @Autowired
    private BillingService billingService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private UserShippingService userShippingService;

    @Autowired
    private UserPaymentService userPaymentService;

    @Autowired
    private MailConstructor mailConstructor;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private OrderService orderService;

    @RequestMapping(value = "/checkout")
    public String checkout(@RequestParam("id") Long cartId, @RequestParam(value = "missingRequiredField", required = false) boolean missingRequiredField,  Model model, Principal principal){
        User user = userService.findByUsername(principal.getName());
        if (cartId!=user.getShoppingCart().getId()){
            return "badRequestPage";
        }
        List<CartItem> cartItemList= cartItemService.findByShopppingCart(user.getShoppingCart());

        if (cartItemList.size()==0){
            model.addAttribute("emptyCart", true);
            return "forward:/shoppingCart/cart";
        }
        for (CartItem cartItem : cartItemList){
            if (cartItem.getBook().getInStockNumber()<cartItem.getQty()){
                model.addAttribute("notEnoughStock", true);
                return "forward:/shoppingCart/cart";
            }
        }

        List<UserShipping> userShippingList = user.getUserShippingList();
        List<UserPayment> userPaymentList = user.getUserPaymentList();
        model.addAttribute("userShippingList", userShippingList);
        model.addAttribute("userPaymentList", userPaymentList);

        if (userShippingList.size()==0){
            model.addAttribute("emptyShippingList", true);
        } else {
            model.addAttribute("emptyShippingList", false);
        }
        if (userPaymentList.size()==0){
            model.addAttribute("emptyPaymentList", true);
        } else {
            model.addAttribute("emptyPaymentList", false);
        }

        ShoppingCart shoppingCart = user.getShoppingCart();

        for (UserShipping userShipping: userShippingList){
            if (userShipping.isUserShippingDefault()){
                shippingAddressService.setByUserShipping(userShipping, shippingAddress);
            }
        }

        for (UserPayment userPayment: userPaymentList){
            if (userPayment.isDefaultPayment()){
                paymentService.setByUserPayment(userPayment, payment);
                billingService.setByUserBilling(userPayment.getUserBilling(), billing);
            }
        }

        model.addAttribute("payment", payment);
        model.addAttribute("billing", billing);
        model.addAttribute("shippingAddress", shippingAddress);
        model.addAttribute("shoppingCart", shoppingCart);
        model.addAttribute("cartItemList", cartItemList);
        model.addAttribute("classActiveShipping",true);

        if (missingRequiredField){
            model.addAttribute("missingRequiredField",true);
        } else {
            model.addAttribute("missingRequiredField",false);

        }

        return "checkout";
    }

    @RequestMapping(value = "/checkout", method = RequestMethod.POST)
    public String checkoutPost(
            @ModelAttribute("shippingAddress") ShippingAddress shippingAddress,
            @ModelAttribute("billing") Billing billing,
            @ModelAttribute("payment") Payment payment,
            @ModelAttribute("billingSameAsShipping") String billingSameAsShipping,
            @ModelAttribute("shippingMethod") String shippingMethod,
            Principal principal, Model model
    ){
        User user = userService.findByUsername(principal.getName());
        ShoppingCart shoppingCart = user.getShoppingCart();

        List<CartItem> cartItemList = cartItemService.findByShopppingCart(shoppingCart);
        model.addAttribute("cartItemList", cartItemList);

        if (billingSameAsShipping.equals("true")){
            billing.setBillingName(shippingAddress.getShippingAddressName());
            billing.setBillingStreet1(shippingAddress.getShippingAddressStreet1());
            billing.setBillingStreet2(shippingAddress.getShippingAddressStreet2());
            billing.setBillingCity(shippingAddress.getShippingAddressCity());
            billing.setBillingZipCode(shippingAddress.getShippingAddressZipCode());
            billing.setBillingCountry(shippingAddress.getShippingAddressCountry());
        }

        if (
                shippingAddress.getShippingAddressStreet1().isEmpty() ||
                shippingAddress.getShippingAddressCity().isEmpty() ||
                shippingAddress.getShippingAddressZipCode().isEmpty() ||
                shippingAddress.getShippingAddressName().isEmpty() ||
                        payment.getHolderName().isEmpty() ||
                        payment.getCardNumber().isEmpty() ||
                        payment.getCvc()==0 ||
                        payment.getExpiryMonth()==0 ||
                        payment.getExpiryYear()==0 ||
                        billing.getBillingStreet1().isEmpty() ||
                        billing.getBillingCity().isEmpty() ||
                        billing.getBillingName().isEmpty() ||
                        billing.getBillingZipCode().isEmpty()
                ){
            return "redirect:/checkout?id="+shoppingCart.getId()+"&missingRequiredField=true";
        }

        Order order = orderService.createOrder(shoppingCart, shippingAddress, billing, payment, shippingMethod, user);

        mailSender.send(mailConstructor.constructOrderConfirmationEmail(user, order, Locale.ENGLISH));

        shoppingCartService.clearShoppingCart(shoppingCart);

        LocalDate today = LocalDate.now();
        LocalDate estimatedDeliveryDate;

        if (shippingMethod.equals("groundShipping")){
            estimatedDeliveryDate = today.plusDays(5);
        } else {
            estimatedDeliveryDate = today.plusDays(3);
        }

        model.addAttribute("estimatedDeliveryDate", estimatedDeliveryDate);

        return "orderSubmitted";
    }

    @RequestMapping(value = "/setShippingAddress")
    public String setShippingAddress(@RequestParam("userShippingId") Long userShippingId, Model model, Principal principal){
        User user = userService.findByUsername(principal.getName());
        UserShipping userShipping = userShippingService.findById(userShippingId);

        if (userShipping.getUser().getId()!=user.getId()){
            return "badRequest";
        }

        shippingAddressService.setByUserShipping(userShipping, shippingAddress);

        List<CartItem> cartItemList= cartItemService.findByShopppingCart(user.getShoppingCart());

        model.addAttribute("payment", payment);
        model.addAttribute("billing", billing);
        model.addAttribute("shippingAddress", shippingAddress);
        model.addAttribute("shoppingCart", user.getShoppingCart());
        model.addAttribute("cartItemList", cartItemList);

        List<UserShipping> userShippingList = user.getUserShippingList();
        List<UserPayment> userPaymentList = user.getUserPaymentList();
        model.addAttribute("userShippingList", userShippingList);
        model.addAttribute("userPaymentList", userPaymentList);

        if (userShippingList.size()==0){
            model.addAttribute("emptyShippingList", true);
        } else {
            model.addAttribute("emptyShippingList", false);
        }
        if (userPaymentList.size()==0){
            model.addAttribute("emptyPaymentList", true);
        } else {
            model.addAttribute("emptyPaymentList", false);
        }

        model.addAttribute("classActiveShipping",true);

        return "checkout";
    }

    @RequestMapping(value = "/setPaymentMethod")
    public String setPaymentMethod(@RequestParam("userPaymentId") Long userPaymentId, Model model, Principal principal){
        User user = userService.findByUsername(principal.getName());
        UserPayment userPayment = userPaymentService.findById(userPaymentId);
        UserBilling userBilling = userPayment.getUserBilling();

        if (userPayment.getUser().getId()!=user.getId()){
            return "badRequest";
        }

        paymentService.setByUserPayment(userPayment, payment);

        List<CartItem> cartItemList= cartItemService.findByShopppingCart(user.getShoppingCart());
        billingService.setByUserBilling(userBilling, billing);

        model.addAttribute("payment", payment);
        model.addAttribute("billing", billing);
        model.addAttribute("shippingAddress", shippingAddress);
        model.addAttribute("shoppingCart", user.getShoppingCart());
        model.addAttribute("cartItemList", cartItemList);

        List<UserShipping> userShippingList = user.getUserShippingList();
        List<UserPayment> userPaymentList = user.getUserPaymentList();
        model.addAttribute("userShippingList", userShippingList);
        model.addAttribute("userPaymentList", userPaymentList);

        if (userShippingList.size()==0){
            model.addAttribute("emptyShippingList", true);
        } else {
            model.addAttribute("emptyShippingList", false);
        }
        if (userPaymentList.size()==0){
            model.addAttribute("emptyPaymentList", true);
        } else {
            model.addAttribute("emptyPaymentList", false);
        }

        model.addAttribute("classActivePayment",true);

        return "checkout";
    }


}

























