package com.zcw.cmall.goods.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zcw.cmall.goods.entity.SpuInfoEntity;
import com.zcw.cmall.goods.vo.SkuItemVo;
import com.zcw.common.utils.PageUtils;
import com.zcw.cmall.goods.entity.SkuInfoEntity;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * sku信息
 *
 * @author Chrisz
 * @email sunlightcs@gmail.com
 * @date 2020-10-19 15:27:36
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSkuInfo(SkuInfoEntity skuInfoEntity);

    PageUtils queryPageByCondition(Map<String, Object> params);

    List<SkuInfoEntity> getSkusBySpuId(Long spuId);

    /**
     * 商品详情页-异步、线程池
     */
    SkuItemVo item(Long skuId) throws ExecutionException, InterruptedException;
}

