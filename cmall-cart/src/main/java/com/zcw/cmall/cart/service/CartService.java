package com.zcw.cmall.cart.service;

import com.zcw.cmall.cart.vo.Cart;
import com.zcw.cmall.cart.vo.CartItem;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author Chrisz
 * @date 2020/12/18 - 9:23
 */
public interface CartService {

    /**
     * 添加商品到购物车
     * @param skuId
     * @param num
     * @return
     */
    CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException;

    /**
     * 获取购物车中某个购物项
     * @param skuId
     * @return
     */
    CartItem getCartItem(Long skuId);

    /**
     * 获取购物车
     * @return
     */
    Cart getCart() throws ExecutionException, InterruptedException;

    /**
     * 清空购物车
     * @param cartKey
     */
    void clearCart(String cartKey);

    /**
     * 改变购物项的选中状态
     * @param skuId
     * @param check
     */
    void checkItem(Long skuId, Integer check);

    /**
     * 改变购物项的数量
     * @param skuId
     * @param num
     */
    void countItem(Long skuId, Integer num);

    /**
     * 删除购物项
     * @param deleteItemSkuId
     */
    void deleteItem(Long deleteItemSkuId);

    /**
     * 获取购物车所有选中的购物项
     * @return
     */
    List<CartItem> getCurrentUserCartItems();
}
