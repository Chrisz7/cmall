package com.zcw.cmall.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Chrisz
 * @date 2020/12/22 - 19:52
 */
@Data
public class OrderItemVo {
    private Long skuId;
    private Boolean check = true;
    private String  title;
    private String image;
    private List<String> skuAttr;
    //BigDecimal 牵扯到计算，所以使用BigDecimal
    private BigDecimal price;
    private Integer count;
    private BigDecimal totalPrice;


    private BigDecimal weight;
}
