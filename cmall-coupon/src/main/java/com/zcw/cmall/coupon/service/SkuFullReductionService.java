package com.zcw.cmall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zcw.common.to.SkuReductionTo;
import com.zcw.common.utils.PageUtils;
import com.zcw.cmall.coupon.entity.SkuFullReductionEntity;
import com.zcw.common.utils.R;

import java.util.Map;

/**
 * 商品满减信息
 *
 * @author Chrisz
 * @email sunlightcs@gmail.com
 * @date 2020-10-19 21:00:46
 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSkuReduction(SkuReductionTo skuReductionTo);
}

