package com.zcw.cmall.stock.vo;

import lombok.Data;

/**
 * @author Chrisz
 * @date 2020/12/26 - 15:24
 */
@Data
public class LockStockResult {

    private Long skuId;
    private Integer num;
    private Boolean locked;
}
