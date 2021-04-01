package com.zcw.cmall.order.to;

import com.zcw.cmall.order.entity.OrderEntity;
import com.zcw.cmall.order.entity.OrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Chrisz
 * @date 2020/12/26 - 11:08
 */
@Data
public class OrderCreateTo {

    private OrderEntity order;
    private List<OrderItemEntity> orderItems;
    private BigDecimal payPrice;
    private BigDecimal fare;
}
