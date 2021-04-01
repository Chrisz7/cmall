package com.zcw.cmall.stock.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zcw.cmall.stock.vo.LockStockResult;
import com.zcw.cmall.stock.vo.SkuHasStockVo;
import com.zcw.cmall.stock.vo.StockSkuLockVo;
import com.zcw.common.to.OrderCloseTo;
import com.zcw.common.to.mq.StockLockedTo;
import com.zcw.common.utils.PageUtils;
import com.zcw.cmall.stock.entity.WareSkuEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author Chrisz
 * @email sunlightcs@gmail.com
 * @date 2020-10-19 21:15:24
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, Integer skuNum);

    /**
     * 查询sku是否有库存
     * @param skuIds
     * @return
     */
    List<SkuHasStockVo> getSkusHasStock(List<Long> skuIds);

    /**
     * 锁库存
     * @param vo
     * @return
     */
    Boolean orderLockStock(StockSkuLockVo vo);

    /**
     * 解锁库存
     * @param to
     */
    void unLockStock(StockLockedTo to);

    /**
     * 订单解锁，库存解锁
     * @param to
     */
    void unLockStock(OrderCloseTo to);
}

