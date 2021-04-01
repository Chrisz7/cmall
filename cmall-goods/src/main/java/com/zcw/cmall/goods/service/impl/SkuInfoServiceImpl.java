package com.zcw.cmall.goods.service.impl;

import com.zcw.cmall.goods.entity.SkuImagesEntity;
import com.zcw.cmall.goods.entity.SpuInfoDescEntity;
import com.zcw.cmall.goods.entity.SpuInfoEntity;
import com.zcw.cmall.goods.service.*;
import com.zcw.cmall.goods.vo.SkuItemSaleAttrsVo;
import com.zcw.cmall.goods.vo.SkuItemVo;
import com.zcw.cmall.goods.vo.SpuItemAttrGroupVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcw.common.utils.PageUtils;
import com.zcw.common.utils.Query;

import com.zcw.cmall.goods.dao.SkuInfoDao;
import com.zcw.cmall.goods.entity.SkuInfoEntity;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    //注入自定义的线程池 MyThreadConfig
    @Autowired
    ThreadPoolExecutor executor;
    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    AttrGroupService attrGroupService;
    @Autowired
    SpuInfoDescService spuInfoDescService;
    @Autowired
    SkuImagesService skuImagesService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
        this.baseMapper.insert(skuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            wrapper.and(w -> {
                w.eq("sku_id",key).or().like("sku_name",key);
            });
        }
        String catalogId = (String) params.get("catalogId");
        if(!StringUtils.isEmpty(catalogId) && !"0".equalsIgnoreCase(catalogId)){
            wrapper.eq("catalog_id",catalogId);
        }
        String brandId = (String) params.get("brandId");
        if(!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)){
            wrapper.eq("brand_id",brandId);
        }
        String min = (String) params.get("min");
        if(!StringUtils.isEmpty(min)){
            wrapper.ge("price",min);
        }
        String max = (String) params.get("max");
        if(!StringUtils.isEmpty(max)){
            try{
                BigDecimal bigDecimal = new BigDecimal(max);
                if(bigDecimal.compareTo(new BigDecimal("0"))==1)
                    wrapper.le("price",max);
            }catch (Exception e){

            }

        }
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuInfoEntity> getSkusBySpuId(Long spuId) {
        List<SkuInfoEntity> skuInfoEntities = this.baseMapper.selectList(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
        return skuInfoEntities;
    }

    /**
     * 商品详情页-异步、线程池
     * @param skuId
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Override
    public SkuItemVo item(Long skuId) throws ExecutionException, InterruptedException {
        SkuItemVo skuItemVo = new SkuItemVo();
        //executor容器中的自定义的线程池
        //supplyAsync创建异步操作,有返回值的  Async异步的方式运行
        CompletableFuture<SkuInfoEntity> infoFuture = CompletableFuture.supplyAsync(() -> {

            //基本信息
            //SkuInfoServiceImpl implements SkuInfoService ->SkuInfoService extends IService-> IService.getById
            SkuInfoEntity info = getById(skuId);
            skuItemVo.setInfo(info);
            return info;
        }, executor);
        //thenAcceptAsync ,能接受上一步的结果，有返回值  不是线程串行化？？？
        CompletableFuture<Void> groupAttrFuture = infoFuture.thenAcceptAsync((res) -> {
            //属性分组以及属性值信息
            List<SpuItemAttrGroupVo> attrGroupVos = attrGroupService.getAttrGroupWithAttrsBySpuId(res.getSpuId(), res.getCatalogId());
            skuItemVo.setGroupAttrs(attrGroupVos);
        }, executor);

        CompletableFuture<Void> descFuture = infoFuture.thenAcceptAsync((res) -> {
            //描述信息
            SpuInfoDescEntity spuInfo = spuInfoDescService.getById(res.getSpuId());
            skuItemVo.setDesc(spuInfo);
        }, executor);

        CompletableFuture<Void> attrFuture = infoFuture.thenAcceptAsync((res) -> {
            //销售属性信息
            List<SkuItemSaleAttrsVo> saleAttrVos = skuSaleAttrValueService.getSaleAttrsBySpuId(res.getSpuId());
            skuItemVo.setSaleAttrs(saleAttrVos);

        }, executor);

        //runAsync 没有返回结果 ，再开启一个异步操作，跟别的没什么关联
        CompletableFuture<Void> imageFuture = CompletableFuture.runAsync(() -> {
            //图片信息
            List<SkuImagesEntity> images = skuImagesService.getImagesBySkuId(skuId);
            skuItemVo.setImages(images);
        }, executor);

        //等待所有异步任务都完成，返回数据  get在这阻塞
        CompletableFuture.allOf(groupAttrFuture,descFuture,attrFuture,imageFuture).get();
        return skuItemVo;
    }

}
