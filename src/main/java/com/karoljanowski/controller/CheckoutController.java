package com.karoljanowski.controller;

import com.karoljanowski.domain.*;
import com.karoljanowski.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

/**
 * Created by Karol Janowski on 2017-06-30.
 */
@Controller
@RequestMapping("/checkout")
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

    @RequestMapping(value = "/checkout")
    public String checkout(@RequestParam("id") Long cartId, @RequestParam(value = "missingRequiredField") boolean missingRequiredField,  Model model, Principal principal){
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
        model.addAttribute("classActiveShipping", true);

        if (missingRequiredField){
            model.addAttribute("missingRequiredField",true);
        }

        return "checkout";
    }



}

























