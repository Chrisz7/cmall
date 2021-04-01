package com.zcw.cmall.goods.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.zcw.cmall.goods.entity.*;
import com.zcw.cmall.goods.feign.CouponFeignService;
import com.zcw.cmall.goods.feign.SearchFeignService;
import com.zcw.cmall.goods.feign.StockFeignService;
import com.zcw.cmall.goods.service.*;
import com.zcw.cmall.goods.vo.*;
import com.zcw.common.constant.GoodsConstant;
import com.zcw.common.to.SkuHasStockVo;
import com.zcw.common.to.SkuReductionTo;
import com.zcw.common.to.SpuBoundTo;
import com.zcw.common.to.es.SkuEsModel;
import com.zcw.common.utils.R;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcw.common.utils.PageUtils;
import com.zcw.common.utils.Query;

import com.zcw.cmall.goods.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    SearchFeignService searchFeignService;
    @Autowired
    StockFeignService stockFeignService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    BrandService brandService;
    @Autowired
    SpuInfoDescService descService;
    @Autowired
    SpuImagesService imagesService;
    @Autowired
    AttrService attrService;
    @Autowired
    ProductAttrValueService attrValueService;
    @Autowired
    SkuInfoService skuInfoService;
    @Autowired
    SkuImagesService skuImagesService;
    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    CouponFeignService couponFeignService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo vo) {
        //1、保存spu基本信息 pms_spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(vo,spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.saveBaseSpuInfo(spuInfoEntity);

        //2、保存Spu的大图介绍 pms_spu_info_desc
        List<String> decript = vo.getDecript();
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
        //很多图集的话用String.join(",",stringxxx)分割开
        spuInfoDescEntity.setDecript(String.join(",",decript));
        descService.saveSpuInfoDesc(spuInfoDescEntity);
        //3、保存spu的图片集 pms_spu_images
        List<String> images = vo.getImages();
        //参数为spuId、images图集
        imagesService.saveImages(spuInfoEntity.getId(),images);


        //4、保存spu的规格参数;pms_product_attr_value
        List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
        List<ProductAttrValueEntity> attrValues = baseAttrs.stream().map(item -> {
            ProductAttrValueEntity attrValueEntity = new ProductAttrValueEntity();
            attrValueEntity.setAttrId(item.getAttrId());
            //根据属性id查出属性entity
            AttrEntity attrEntity = attrService.getById(item.getAttrId());
            attrValueEntity.setAttrName(attrEntity.getAttrName());
            attrValueEntity.setAttrValue(item.getAttrValues());
            attrValueEntity.setQuickShow(item.getShowDesc());
            attrValueEntity.setSpuId(spuInfoEntity.getId());
            return attrValueEntity;
        }).collect(Collectors.toList());
        //保存spu
        attrValueService.saveProductAttr(attrValues);

        //5、保存spu的积分信息；gulimall_sms->sms_spu_bounds
        Bounds bounds = vo.getBounds();
        SpuBoundTo spuBoundTo = new SpuBoundTo();
        //多了一个spuid
        BeanUtils.copyProperties(bounds,spuBoundTo);
        spuBoundTo.setSpuId(spuInfoEntity.getId());
        //Feign跨服务远程调用
        R r = couponFeignService.saveSpuBounds(spuBoundTo);
        if(r.getCode() != 0){
            log.error("远程保存spu积分信息失败");
        }

        //5、保存当前spu对应的所有sku信息；

        List<Skus> skus = vo.getSkus();
        if(skus!=null && skus.size()>0){
            skus.forEach(item->{
                String defaultImg = "";
                for (Images image : item.getImages()) {
                    if(image.getDefaultImg() == 1){
                        defaultImg = image.getImgUrl();
                    }
                }
                //    private String skuName;
                //    private BigDecimal price;
                //    private String skuTitle;
                //    private String skuSubtitle;
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(item,skuInfoEntity);
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                skuInfoEntity.setSkuDefaultImg(defaultImg);
                //5.1）、sku的基本信息；pms_sku_info
                skuInfoService.saveSkuInfo(skuInfoEntity);

                Long skuId = skuInfoEntity.getSkuId();

                List<SkuImagesEntity> imagesEntities = item.getImages().stream().map(img -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(img.getImgUrl());
                    skuImagesEntity.setDefaultImg(img.getDefaultImg());
                    return skuImagesEntity;
                }).filter(entity->{
                    //返回true就是需要，false就是剔除
                    return !StringUtils.isEmpty(entity.getImgUrl());
                }).collect(Collectors.toList());
                //5.2）、sku的图片信息；pms_sku_image
                skuImagesService.saveBatch(imagesEntities);
                //TODO 没有图片路径的无需保存

                List<Attr> attr = item.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attr.stream().map(a -> {
                    SkuSaleAttrValueEntity attrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(a, attrValueEntity);
                    attrValueEntity.setSkuId(skuId);

                    return attrValueEntity;
                }).collect(Collectors.toList());
                //5.3）、sku的销售属性信息：pms_sku_sale_attr_value
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);

                // //5.4）、sku的优惠、满减等信息；gulimall_sms->sms_sku_ladder\sms_sku_full_reduction\sms_member_price
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(item,skuReductionTo);
                skuReductionTo.setSkuId(skuId);
                if(skuReductionTo.getFullCount() >0 || skuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) == 1){
                    R r1 = couponFeignService.saveSkuReduction(skuReductionTo);
                    if(r1.getCode() != 0){
                        log.error("远程保存sku优惠信息失败");
                    }
                }



            });
        }
    }

    //1、保存spu基本信息 pms_spu_info
    @Override
    public void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
        this.baseMapper.insert(spuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            //使用这样的方式，前面的一些条件都需要这一个整体作为查询条件，所以是。。
            wrapper.and(w -> {
                w.eq("id",key).or().like("spu_name",key);
            });
        }
        String status = (String) params.get("status");
        if(!StringUtils.isEmpty(status)){
            wrapper.eq("publish_status",status);
        }
        String brandId = (String) params.get("brandId");
        if(!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)){
            wrapper.eq("brand_id",brandId);
        }
        String catelogId = (String) params.get("catalogId");
        if(!StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)){
            wrapper.eq("catalog_id",catelogId);
        }

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    /**
     * 商品上架
     * @param spuId
     */
    @Override
    public void up(Long spuId) {

        //1.组装数据
        //SkuEsModel skuEsModel = new SkuEsModel();
        //根据spuId查出所有sku信息，还有品牌的名称?
        List<SkuInfoEntity> skuInfoEntities = skuInfoService.getSkusBySpuId(spuId);
        List<Long> skuIdList = skuInfoEntities.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());

        //TODO 查询当前sku所有可以被检索的规格属性
        List<ProductAttrValueEntity> base = attrValueService.baseAttrListForSpu(spuId);
        //这个stream操作直接一步步走，就过去了，在debug过程中，修改了某些地方，整个调试过程出问题
        List<Long> ids = base.stream().map(attr -> {
            return attr.getAttrId();
        }).collect(Collectors.toList());


        List<Long> searchAttrIds = attrService.selectSearchAttrs(ids);
        Set<Long> idSet = new HashSet<>(searchAttrIds);


        List<SkuEsModel.Attr> collect = base.stream().filter(attr -> {
            return idSet.contains(attr.getAttrId());
        }).map(attr -> {

            SkuEsModel.Attr attr1 = new SkuEsModel.Attr();
            BeanUtils.copyProperties(attr, attr1);
            return attr1;
        }).collect(Collectors.toList());
        //TODO hasStock(远程调用库存系统，是否还有库存),hotScore(默认 0)

        Map<Long, Boolean> map = null;
        try{
            R r = stockFeignService.getSkusHasStock(skuIdList);
            //这里接收的R设置data的时候，key是data，值时list，要转成list
            //new TypeReference<List<SkuHasStockVo>>(){}  相当于  new了一个TypeReference的子类，这样使用是因为 protected TypeReference() {} 只能子类使用
            //debug中的计算按钮
            map = r.getData(new TypeReference<List<SkuHasStockVo>>(){}).stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, item -> item.getHasStock()));

        }catch (Exception e){
            log.error("库存查询异常:{}",e);
        }
        //封装每个sku信息
        Map<Long, Boolean> finalMap = map;
        List<SkuEsModel> upGoods = skuInfoEntities.stream().map(sku -> {
            SkuEsModel esModel = new SkuEsModel();
            BeanUtils.copyProperties(sku,esModel);
            esModel.setSkuPrice(sku.getPrice());
            esModel.setSkuImg(sku.getSkuDefaultImg());

            if(finalMap ==null){
                esModel.setHasStock(true);
            }
            else{
                esModel.setHasStock(finalMap.get(sku.getSkuId()));
            }

            esModel.setHotScore(0L);

            BrandEntity brand = brandService.getById(esModel.getBrandId());
            esModel.setBrandName(brand.getName());
            esModel.setBrandImg(brand.getLogo());

            CategoryEntity category = categoryService.getById(esModel.getCatalogId());
            esModel.setCatalogName(category.getName());

            //设置检索属性
            esModel.setAttrs(collect);
            //TODO
            return esModel;
        }).collect(Collectors.toList());
        //TODO 将数据发送给es保存  cmall-search

        R r = searchFeignService.goodStatusUp(upGoods);
        if (r.getCode()==0){
            this.baseMapper.upSpuStatus(spuId, GoodsConstant.StatusEnum.UP_SPU.getCode());
        }else {
            log.error("商品远程es保存失败");
        }
    }

    /**
     * 根据skuId查spu信息
     * @param skuIdg
     * @return
     */
    @Override
    public SpuInfoEntity getSpuInfoBySkuId(Long skuId) {
        SkuInfoEntity skuInfoEntity = skuInfoService.getById(skuId);
        Long spuId = skuInfoEntity.getSpuId();
        SpuInfoEntity spu = getById(spuId);
        return spu;
    }


}
