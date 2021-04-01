package com.zcw.cmall.stock.service.impl;

import com.zcw.cmall.stock.entity.PurchaseDetailEntity;
import com.zcw.cmall.stock.service.PurchaseDetailService;
import com.zcw.cmall.stock.service.WareSkuService;
import com.zcw.cmall.stock.vo.MergeVo;
import com.zcw.cmall.stock.vo.PurchaseDoneVo;
import com.zcw.cmall.stock.vo.PurchaseItemDoneVo;
import com.zcw.common.constant.StockConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcw.common.utils.PageUtils;
import com.zcw.common.utils.Query;

import com.zcw.cmall.stock.dao.PurchaseDao;
import com.zcw.cmall.stock.entity.PurchaseEntity;
import com.zcw.cmall.stock.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    PurchaseDetailService detailService;
    @Autowired
    WareSkuService wareSkuService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageUnreceivePurchase(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>().eq("status",0).or().eq("status",1)
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void mergePurchase(MergeVo vo) {
        Long purchaseId = vo.getPurchaseId();
        if(purchaseId == null){
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setStatus(StockConstant.PurchaseEnum.CREATED.getCode());
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            this.save(purchaseEntity);
            purchaseId = purchaseEntity.getId();

        }
        List<Long> items = vo.getItems();
        //????
        Long finalPurchaseId = purchaseId;
        List<PurchaseDetailEntity> detailEntities = items.stream().map(i -> {
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            purchaseDetailEntity.setId(i);
            purchaseDetailEntity.setPurchaseId(finalPurchaseId);
            purchaseDetailEntity.setStatus(StockConstant.PurchaseDetailEnum.ASSIGNED.getCode());
            return purchaseDetailEntity;
        }).collect(Collectors.toList());

        detailService.updateBatchById(detailEntities);

        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(purchaseId);
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);
    }

    @Override
    public void received(List<Long> ids) {

        //确认当前采购单是新建的或分配给自己的，自己才能领取
        List<PurchaseEntity> collect = ids.stream().map(id -> {
            PurchaseEntity entity = this.getById(id);
            return entity;
        }).filter(item -> {
            if (item.getStatus() == StockConstant.PurchaseEnum.CREATED.getCode()
                    || item.getStatus() == StockConstant.PurchaseEnum.ASSIGNED.getCode()) {
                return true;
            } else {
                return false;
            }
        }).map(item -> {
            item.setStatus(StockConstant.PurchaseEnum.RECEIVED.getCode());
            item.setUpdateTime(new Date());
            return item;
        }).collect(Collectors.toList());

        //改变采购单的状态
        this.updateBatchById(collect);

        //改变采购项的状态
        collect.forEach( item -> {
            List<PurchaseDetailEntity> entities = detailService.listDetailByPurchaseId(item.getId());
            List<PurchaseDetailEntity> detailEntities = entities.stream().map(entity -> {
                PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
                purchaseDetailEntity.setId(entity.getId());
                purchaseDetailEntity.setStatus(StockConstant.PurchaseDetailEnum.BUYING.getCode());
                return purchaseDetailEntity;
            }).collect(Collectors.toList());
            detailService.updateBatchById(detailEntities);
        });

    }

    @Transactional
    @Override
    public void done(PurchaseDoneVo doneVo) {

         Long id = doneVo.getId();


         Boolean flag = true;
        List<PurchaseItemDoneVo> items = doneVo.getItems();
        List<PurchaseDetailEntity> updates = new ArrayList<>();
        for (PurchaseItemDoneVo item : items) {
            PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
            if(item.getStatus() == StockConstant.PurchaseDetailEnum.HASHERROR.getCode()){
                flag = false;
                detailEntity.setStatus(item.getStatus());
            }else {
                detailEntity.setStatus(StockConstant.PurchaseDetailEnum.FINISHED.getCode());
                PurchaseDetailEntity e = detailService.getById(item.getItemId());
                wareSkuService.addStock(e.getSkuId(),e.getWareId(),e.getSkuNum());
            }
            detailEntity.setId(item.getItemId());
            updates.add(detailEntity);

        }
        detailService.updateBatchById(updates);

        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(id);
        purchaseEntity.setStatus(flag ? StockConstant.PurchaseDetailEnum.FINISHED.getCode() : StockConstant.PurchaseDetailEnum.HASHERROR.getCode());
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);



    }

}
