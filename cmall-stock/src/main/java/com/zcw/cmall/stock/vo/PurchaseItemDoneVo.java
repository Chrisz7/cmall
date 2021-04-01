package com.zcw.cmall.stock.vo;

import lombok.Data;

/**
 * @author Chrisz
 * @date 2020/11/12 - 9:40
 */
@Data
public class PurchaseItemDoneVo {
    private Long itemId;
    private Integer status;
    private String reason;
}
