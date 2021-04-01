package com.zcw.cmall.stock.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Chrisz
 * @date 2020/11/12 - 9:39
 */
@Data
public class PurchaseDoneVo {
    @NotNull
    private Long id;

    private List<PurchaseItemDoneVo> items;
}
