package com.zcw.cmall.stock.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Chrisz
 * @date 2020/12/26 - 15:19
 */
@Data
public class StockSkuLockVo {

    private String orderSn;
    private List<OrderItemVo> locks;//
}
