package com.zcw.cmall.order.vo;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * @author Chrisz
 * @date 2020/12/26 - 10:25
 */
@ToString
@Data
public class OrderSubmitVo {

    private Long addrId;
    private Integer payType;
    //无需提交需要购买的商品，去购物车在获取一遍
    private String orderToken;
    private BigDecimal payPrice;//验价
    //用户信息，session中取
    private String note;//备注

}
