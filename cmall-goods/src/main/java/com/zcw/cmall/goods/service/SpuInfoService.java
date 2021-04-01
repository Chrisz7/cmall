package com.zcw.cmall.goods.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zcw.cmall.goods.entity.SpuInfoDescEntity;
import com.zcw.cmall.goods.vo.SpuSaveVo;
import com.zcw.common.utils.PageUtils;
import com.zcw.cmall.goods.entity.SpuInfoEntity;

import java.util.Map;

/**
 * spu信息
 *
 * @author Chrisz
 * @email sunlightcs@gmail.com
 * @date 2020-10-19 15:27:36
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSpuInfo(SpuSaveVo vo);

    void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity);


    PageUtils queryPageByCondition(Map<String, Object> params);

    /**
     * 商品上架
     * @param spuId
     */
    void up(Long spuId);

    /**
     * 根据skuId查spu信息
     * @param skuId
     * @return
     */
    SpuInfoEntity getSpuInfoBySkuId(Long skuId);
}

