package com.zcw.cmall.stock.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.zcw.cmall.stock.vo.SkuHasStockVo;
import com.zcw.cmall.stock.vo.StockSkuLockVo;
import com.zcw.common.exception.ExceCodeEnum;
import com.zcw.common.exception.NoStockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.zcw.cmall.stock.entity.WareSkuEntity;
import com.zcw.cmall.stock.service.WareSkuService;
import com.zcw.common.utils.PageUtils;
import com.zcw.common.utils.R;



/**
 * 商品库存
 *
 * @author Chrisz
 * @email sunlightcs@gmail.com
 * @date 2020-10-19 21:15:24
 */
@RestController
@RequestMapping("stock/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;

    /**
     * 锁库存
     * @param vo
     * @return
     */
    @PostMapping("/orderLockStock")
    public R orderLockStock(@RequestBody StockSkuLockVo vo){

        try {
            Boolean results = wareSkuService.orderLockStock(vo);
            return R.ok();
        }catch (NoStockException e){
            //TODO 库存不足
            return R.error(ExceCodeEnum.NO_STOCK_EXCEPTION.getCode(),ExceCodeEnum.NO_STOCK_EXCEPTION.getMsg());
        }
    }

    /**
     * 检查库存
     * @param skuIds
     * @return
     */
    //查询sku是否有库存,@RequestBody 将请求体中的数据转换成 List<Long> skuIds
    @PostMapping("/hasstock")
    public R getSkusHasStock(@RequestBody List<Long> skuIds){

        List<SkuHasStockVo> vos = wareSkuService.getSkusHasStock(skuIds);


        //这里R设置data的时候，key是data，值时list，要转成list
        //在return上的debug可以  在程序的后面打一个断点，放行到这个后面的断点
        return R.ok().setData(vos);
    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("stock:waresku:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareSkuService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("stock:waresku:info")
    public R info(@PathVariable("id") Long id){
		WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("stock:waresku:save")
    public R save(@RequestBody WareSkuEntity wareSku){
		wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("stock:waresku:update")
    public R update(@RequestBody WareSkuEntity wareSku){
		wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("stock:waresku:delete")
    public R delete(@RequestBody Long[] ids){
		wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
