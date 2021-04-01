package com.zcw.cmall.order.vo;

import com.zcw.cmall.order.entity.OrderEntity;
import lombok.Data;
import lombok.ToString;

/**
 * @author Chrisz
 * @date 2020/12/26 - 10:49
 */
@Data
@ToString
public class OrderSubmitRespVo {

    private OrderEntity order;
    private Integer code;//0成功  错误状态码
}
