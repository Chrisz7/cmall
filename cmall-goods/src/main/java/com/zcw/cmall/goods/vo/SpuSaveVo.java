/**
  * Copyright 2020 bejson.com
  */
package com.zcw.cmall.goods.vo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Auto-generated: 2020-11-05 17:3:37
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class SpuSaveVo {

    private String spuName;
    private String spuDescription;
    private Long catalogId;
    private Long brandId;
    //小数不用Double,用BigDecimal,更加精确
    private BigDecimal weight;
    private int publishStatus;
    //大图介绍
    private List<String> decript;
    //图集
    private List<String> images;
    //规格参数
    private List<BaseAttrs> baseAttrs;
    //积分信息
    private Bounds bounds;
    //销售属性
    private List<Skus> skus;

}
