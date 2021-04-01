package com.zcw.cmall.goods.web;

import com.zcw.cmall.goods.service.SkuInfoService;
import com.zcw.cmall.goods.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.concurrent.ExecutionException;

/**
 * @author Chrisz
 * @date 2020/12/2 - 11:39
 */
@Controller
public class ItemController {

    /**
     * 商品详情页-异步、线程池
     */
    @Autowired
    SkuInfoService skuInfoService;
    @GetMapping("/{skuId}.html")
    public String skuItem(@PathVariable("skuId") Long skuId, Model model) throws ExecutionException, InterruptedException {

        SkuItemVo skuItemVo = skuInfoService.item(skuId);
        model.addAttribute("item",skuItemVo);
        return "item";
    }
}
