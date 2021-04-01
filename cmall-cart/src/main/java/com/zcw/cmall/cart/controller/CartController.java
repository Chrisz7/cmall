package com.zcw.cmall.cart.controller;

import com.zcw.cmall.cart.interceptor.CartInterceptor;
import com.zcw.cmall.cart.service.CartService;
import com.zcw.cmall.cart.vo.Cart;
import com.zcw.cmall.cart.vo.CartItem;
import com.zcw.cmall.cart.vo.UserInfoTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author Chrisz
 * @date 2020/12/18 - 9:29
 */
@Controller
public class CartController {

    @Autowired
    CartService cartService;


    /**
     * 获取购物车所有选中的购物项
     * @return
     */
    @GetMapping("/currentUserCartItems")
    @ResponseBody
    public List<CartItem> getCurrentUserCartItems(){

        return cartService.getCurrentUserCartItems();
    }
    /**
     * 删除购物项
     * @param deleteItemSkuId
     * @return
     */
    @GetMapping("/deleteItem")
    public String deleteItem(@RequestParam("deleteItemSkuId") Long deleteItemSkuId){

        cartService.deleteItem(deleteItemSkuId);
        return "redirect:http://cart.cmall.com/cartList.html";
    }
    /**
     * 改变购物项的数量
     * @param skuId
     * @param num
     * @return
     */
    @GetMapping("/countItem")
    public String countItem(@RequestParam("skuId") Long skuId,
                            @RequestParam("num") Integer num){
        cartService.countItem(skuId,num);
        return "redirect:http://cart.cmall.com/cartList.html";
    }

    /**
     * 改变购物项的选中状态
     * @param skuId
     * @param check
     * @return
     */
    @GetMapping("/checkItem")
    public String checkItem(@RequestParam("skuId") Long skuId,
                            @RequestParam("check") Integer check){
        cartService.checkItem(skuId,check);

        return "redirect:http://cart.cmall.com/cartList.html";
    }
    /**
     * 获取购物车
     * @param model
     * @return
     */
    //有一个拦截器CartInterceptor
    @GetMapping("/cartList.html")
    public String cartListPage(Model model) throws ExecutionException, InterruptedException {
//        //从threadLocal获取CartInterceptor封装好的数据
//        UserInfoTo userInfoTO = CartInterceptor.threadLocal.get();
//        System.out.println(userInfoTO);
        //获取购物车
        Cart cart = cartService.getCart();
        model.addAttribute("cart",cart);
        return "cartList";
    }

    /**
     * 添加商品到购物车
     * 为什么使用@GetMapping？
     *
     *只有通过点击加入购物车按钮才能真正加入到购物车，
     * 使用重定向的方式来获取成功把商品添加到购物车中的商品信息
     * @RequestParam 可以获取请求地址中的参数
     * @return
     */
    @GetMapping("/addToCart")
    public String addToCart(@RequestParam("skuId") Long skuId,
                            @RequestParam("num") Integer num,
                            RedirectAttributes attributes) throws ExecutionException, InterruptedException {

        cartService.addToCart(skuId,num);
        //往model中放数据，如果是转发会放在请求域中，如果是重定向会拼到url地址后面
        //重定向携带数据
        //addFlashAttribute()模拟session,将数据放在session中，只能用一次，刷新也没了
        //addAttribute()添加一个属性，将数据放在url后面，拼接上
        attributes.addAttribute("skuId",skuId);
        return "redirect:http://cart.cmall.com/addToCartSuccess.html";
    }

    //重定向到success
    @GetMapping("/addToCartSuccess.html")
    public String addToCartSuccess(@RequestParam("skuId") Long skuId,Model model){

        CartItem cartItem = cartService.getCartItem(skuId);
        model.addAttribute("cartItem",cartItem);
        return "success";
    }
}
