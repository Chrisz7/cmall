package com.zcw.common.to.mq;

import lombok.Data;

import java.util.List;

/**
 * @author Chrisz
 * @date 2020/12/30 - 19:52
 */
@Data
public class StockLockedTo {

    private Long id;//库存工作单id

    private StockDetailTo detail;//工作单详情id
}
