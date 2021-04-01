package com.zcw.cmall.goods.app;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.zcw.cmall.goods.entity.SkuInfoEntity;
import com.zcw.cmall.goods.service.SkuInfoService;
import com.zcw.common.utils.PageUtils;
import com.zcw.common.utils.R;



/**
 * sku信息
 *
 * @author Chrisz
 * @email sunlightcs@gmail.com
 * @date 2020-10-19 17:07:59
 */
@RestController
@RequestMapping("goods/skuinfo")
public class SkuInfoController {
    @Autowired
    private SkuInfoService skuInfoService;

    /**
     * 获取购物项最新的价格
     * @param skuId
     * @return
     */
    @GetMapping("/{skuId}/price")
    public R getPrice(@PathVariable("skuId") Long skuId){
        SkuInfoEntity entity = skuInfoService.getById(skuId);
        return R.ok().setData(entity.getPrice().toString());
    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("goods:skuinfo:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = skuInfoService.queryPageByCondition(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{skuId}")
    //@RequiresPermissions("goods:skuinfo:info")
    public R info(@PathVariable("skuId") Long skuId){
		SkuInfoEntity skuInfo = skuInfoService.getById(skuId);

        return R.ok().put("skuInfo", skuInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("goods:skuinfo:save")
    public R save(@RequestBody SkuInfoEntity skuInfo){
		skuInfoService.save(skuInfo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("goods:skuinfo:update")
    public R update(@RequestBody SkuInfoEntity skuInfo){
		skuInfoService.updateById(skuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("goods:skuinfo:delete")
    public R delete(@RequestBody Long[] skuIds){
		skuInfoService.removeByIds(Arrays.asList(skuIds));

        return R.ok();
    }

}
