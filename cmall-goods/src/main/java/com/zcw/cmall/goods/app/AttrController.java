package com.zcw.cmall.goods.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.zcw.cmall.goods.entity.ProductAttrValueEntity;
import com.zcw.cmall.goods.feign.CouponFeignService;
import com.zcw.cmall.goods.service.ProductAttrValueService;
import com.zcw.cmall.goods.vo.AttrRespVo;
import com.zcw.cmall.goods.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.zcw.cmall.goods.service.AttrService;
import com.zcw.common.utils.PageUtils;
import com.zcw.common.utils.R;



/**
 * 商品属性
 *
 * @author Chrisz
 * @email sunlightcs@gmail.com
 * @date 2020-10-19 17:07:59
 */
@RestController
@RequestMapping("goods/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;

    @Autowired
    CouponFeignService couponFeignService;

    @Autowired
    ProductAttrValueService productAttrValueService;

//    @RequestMapping("/coupons")
//    public R test(){
//        AttrEntity attrEntity = new AttrEntity();
//        attrEntity.setAttrName("苹果XXX");
//
//        R goodsCoupons = couponFeignService.goodsCoupons();
//        return R.ok().put("goods",attrEntity).put("coupons",goodsCoupons.get("coupons"));
//    }

    ///goods/attr/base/listforspu/{spuId}

    @GetMapping("/base/listforspu/{spuId}")
    //@RequiresPermissions("goods:attr:list")
    public R baseAttrListForSpu(@PathVariable("spuId") Long spuId){
        List<ProductAttrValueEntity> entities = productAttrValueService.baseAttrListForSpu(spuId);
        return R.ok().put("data",entities);
    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("goods:attr:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }
    /**
     *
     * @param params 请求参数封装成map
     * @param catelogId
     * @return
     */
    // /product/attr/base/list/{catelogId}
    // /product/attr/sale/list/{catelogId}
    @GetMapping("/{attrType}/list/{catelogId}")
    public R baseList(@RequestParam Map<String, Object> params,
                      @PathVariable("catelogId") Long catelogId,
                      @PathVariable("attrType") String attrType){
        PageUtils page = attrService.queryBaseAttrPage(params,catelogId,attrType);
        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    //@RequiresPermissions("goods:attr:info")
    public R info(@PathVariable("attrId") Long attrId){
		//AttrEntity attr = attrService.getById(attrId);
        AttrRespVo attrRespVo = attrService.getAttrInfo(attrId);
        return R.ok().put("attr", attrRespVo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("goods:attr:save")
    public R save(@RequestBody AttrVo attr){
		attrService.saveAttr(attr);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("goods:attr:update")
    public R update(@RequestBody AttrVo attr){
		attrService.updateAttr(attr);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("goods:attr:delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}
