package com.zcw.cmall.cart.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 整个购物车
 * @author Chrisz
 * @date 2020/12/17 - 11:30
 */

public class Cart {

    List<CartItem> items;
    private Integer countNum;//全部种类商品得数量
    private Integer countType;//全部商品种类数量
    private BigDecimal totalAmount;//总价
    private BigDecimal discount = new BigDecimal("0.00");//优惠

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public Integer getCountNum() {
        int count = 0;
        if (items!=null & items.size()>0){
            for (CartItem item : items) {
                count+=item.getCount();
            }
        }
        return count;
    }

    public Integer getCountType() {
        int count = 0;
        if (items!=null & items.size()>0){
            for (CartItem item : items) {
                count+=1;
            }
        }
        return count;
    }

    public BigDecimal getTotalAmount() {
        BigDecimal amount = new BigDecimal("0.00");
        if (items!=null && items.size()>0){
            for (CartItem item : items) {
                if (item.getCheck()){
                    BigDecimal totalPrice = item.getTotalPrice();
                    amount = amount.add(totalPrice);
                }

            }
        }

        //减去优惠
        BigDecimal subtract = amount.subtract(getDiscount());
        return subtract;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }
}
