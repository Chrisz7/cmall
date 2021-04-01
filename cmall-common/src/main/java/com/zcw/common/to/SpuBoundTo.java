package com.zcw.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Chrisz
 * @date 2020/11/6 - 9:52
 */
@Data
public class SpuBoundTo {

    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}
