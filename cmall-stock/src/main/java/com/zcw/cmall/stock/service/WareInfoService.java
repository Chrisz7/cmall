package com.zcw.cmall.stock.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zcw.cmall.stock.vo.FareVo;
import com.zcw.common.utils.PageUtils;
import com.zcw.cmall.stock.entity.WareInfoEntity;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 仓库信息
 *
 * @author Chrisz
 * @email sunlightcs@gmail.com
 * @date 2020-10-19 21:15:24
 */
public interface WareInfoService extends IService<WareInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 获取运费信息
     * @param addrId
     * @return
     */
    FareVo getFare(Long addrId);
}

