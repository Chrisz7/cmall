package com.zcw.cmall.goods.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zcw.cmall.goods.vo.SkuItemSaleAttrsVo;
import com.zcw.common.utils.PageUtils;
import com.zcw.cmall.goods.entity.SkuSaleAttrValueEntity;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author Chrisz
 * @email sunlightcs@gmail.com
 * @date 2020-10-19 15:27:36
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<SkuItemSaleAttrsVo> getSaleAttrsBySpuId(Long spuId);

    /**
     * 销售属性组合信息
     * @param skuId
     * @return
     */
    List<String> getSkuSaleAttrValuesAsStringList(Long skuId);
}

