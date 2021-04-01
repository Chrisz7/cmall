package com.zcw.cmall.stock.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Chrisz
 * @date 2020/12/24 - 11:53
 */
@Data
public class FareVo {
    private MemberAddressVo address;
    private BigDecimal fare;
}
