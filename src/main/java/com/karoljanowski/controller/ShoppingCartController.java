package com.karoljanowski.controller;

import com.karoljanowski.domain.Book;
import com.karoljanowski.domain.CartItem;
import com.karoljanowski.domain.ShoppingCart;
import com.karoljanowski.domain.User;
import com.karoljanowski.service.BookService;
import com.karoljanowski.service.CartItemService;
import com.karoljanowski.service.ShoppingCartService;
import com.karoljanowski.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.Principal;
import java.util.List;

/**
 * Created by Karol Janowski on 2017-06-30.
 */
@Controller
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private UserService userService;

    @Autowired
    private CartItemService cartItemService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private BookService bookService;


    @RequestMapping(value = "/cart")
    public String shoppingCart(Model model, Principal principal){
        User user = userService.findByUsername(principal.getName());
        ShoppingCart shoppingCart = user.getShoppingCart();

        List<CartItem> cartItemList =cartItemService.findByShopppingCart(shoppingCart);

        shoppingCartService.updateShoppingCart(shoppingCart);

        model.addAttribute("shoppingCart", shoppingCart);
        model.addAttribute("cartItemList", cartItemList);
        model.addAttribute("user", user);

        return "shoppingCart";
    }

    @RequestMapping(value = "/addItem", method = RequestMethod.POST)
    public String addItem(@ModelAttribute("book") Book book, @ModelAttribute("qty") String qty,  Model model, Principal principal){
        System.out.println(book.getId());
        System.out.println("elo");
        User user = userService.findByUsername(principal.getName());
        book = bookService.findOne(book.getId());

        if (Integer.parseInt(qty)>book.getInStockNumber()){
            model.addAttribute("notEnoughStock", true);
            return "forward:/bookDetail?id="+book.getId();
        }

        CartItem cartItem = cartItemService.addBookToCartItem(book, user, Integer.parseInt(qty));
        model.addAttribute("addBookSuccess", true);


        return "forward:/bookDetail?id="+book.getId();
    }

    @RequestMapping(value = "/updateCartItem")
    public String updateCartItem(@ModelAttribute("qty") String qty, @ModelAttribute("id") Long cartItemId, Model model, Principal principal){

        CartItem cartItem = cartItemService.findById(cartItemId);
        cartItem.setQty(Integer.parseInt(qty));
        cartItemService.updateCartItem(cartItem);

        return "forward:/shoppingCart/cart";
    }

    @RequestMapping(value = "/remove")
    public String removeCartItem( @ModelAttribute("id") Long cartItemId){

        cartItemService.removeById(cartItemService.findById(cartItemId));

        return "forward:/shoppingCart/cart";
    }

}

























