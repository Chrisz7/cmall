package com.zcw.cmall.goods.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author Chrisz
 * @date 2020/12/2 - 20:27
 */
@ToString
@Data
public class SkuItemSaleAttrsVo {

    private Long attrId;
    private String attrName;
    private List<AttrValueWithSkuIdVo> attrValues;
}
