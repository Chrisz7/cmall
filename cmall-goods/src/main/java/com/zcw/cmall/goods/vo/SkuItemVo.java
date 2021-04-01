package com.zcw.cmall.goods.vo;

import com.zcw.cmall.goods.entity.SkuImagesEntity;
import com.zcw.cmall.goods.entity.SkuInfoEntity;
import com.zcw.cmall.goods.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

/**
 * @author Chrisz
 * @date 2020/12/2 - 19:33
 */
@Data
public class SkuItemVo {

    SkuInfoEntity info;//sku基本信息
    boolean hasStock = true;//库存
    List<SkuImagesEntity> images;//sku图片信息
    SpuInfoDescEntity desc;//spu介绍
    List<SkuItemSaleAttrsVo> saleAttrs;//spu销售属性组合
    List<SpuItemAttrGroupVo> groupAttrs;//spu的规格参数信息
}
