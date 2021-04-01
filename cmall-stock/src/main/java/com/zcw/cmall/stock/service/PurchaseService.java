package com.zcw.cmall.stock.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zcw.cmall.stock.vo.MergeVo;
import com.zcw.cmall.stock.vo.PurchaseDoneVo;
import com.zcw.common.utils.PageUtils;
import com.zcw.cmall.stock.entity.PurchaseEntity;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author Chrisz
 * @email sunlightcs@gmail.com
 * @date 2020-10-19 21:15:24
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageUnreceivePurchase(Map<String, Object> params);

    void mergePurchase(MergeVo vo);

    void received(List<Long> ids);

    void done(PurchaseDoneVo doneVo);
}

