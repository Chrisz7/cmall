package com.zcw.cmall.stock.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.zcw.cmall.stock.vo.MergeVo;
import com.zcw.cmall.stock.vo.PurchaseDoneVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.zcw.cmall.stock.entity.PurchaseEntity;
import com.zcw.cmall.stock.service.PurchaseService;
import com.zcw.common.utils.PageUtils;
import com.zcw.common.utils.R;



/**
 * 采购信息
 *
 * @author Chrisz
 * @email sunlightcs@gmail.com
 * @date 2020-10-19 21:15:24
 */
@RestController
@RequestMapping("stock/purchase")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;

    /**
     * 完成采购
     * @param ids
     * @return
     */
    ///stock/purchase/done
    @PostMapping("/done")
    public R done(@RequestBody PurchaseDoneVo doneVo){

        purchaseService.done(doneVo);
        return R.ok();
    }

    /**
     * 采购
     * @param ids
     * @return
     */
    @PostMapping("/received")
    public R received(@RequestBody List<Long> ids){

        purchaseService.received(ids);
        return R.ok();
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("stock:purchase:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 获取未分配的采购单
     * @param params
     * @return
     */
    ///stock/purchase/unreceive/list
    @RequestMapping("/unreceive/list")
    //@RequiresPermissions("stock:purchase:list")
    public R unreceiveList(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPageUnreceivePurchase(params);

        return R.ok().put("page", page);
    }

    ///stock/purchase/merge
    @PostMapping("/merge")
    //把请求体中的json包装成这个对象
    public R Merge(@RequestBody MergeVo vo){
        purchaseService.mergePurchase(vo);
        return R.ok();
    }
    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("stock:purchase:info")
    public R info(@PathVariable("id") Long id){
		PurchaseEntity purchase = purchaseService.getById(id);

        return R.ok().put("purchase", purchase);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("stock:purchase:save")
    public R save(@RequestBody PurchaseEntity purchase){
        purchase.setCreateTime(new Date());
        purchase.setUpdateTime(new Date());
		purchaseService.save(purchase);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("stock:purchase:update")
    public R update(@RequestBody PurchaseEntity purchase){
		purchaseService.updateById(purchase);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("stock:purchase:delete")
    public R delete(@RequestBody Long[] ids){
		purchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
