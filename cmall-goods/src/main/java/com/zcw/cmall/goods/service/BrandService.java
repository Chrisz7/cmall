package com.zcw.cmall.goods.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.zcw.common.utils.PageUtils;
import com.zcw.cmall.goods.entity.BrandEntity;

import java.util.Map;

/**
 * 品牌
 *
 * @author Chrisz
 * @email sunlightcs@gmail.com
 * @date 2020-10-19 15:27:36
 */
public interface BrandService extends IService<BrandEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void updateDetail(BrandEntity brand);
}

