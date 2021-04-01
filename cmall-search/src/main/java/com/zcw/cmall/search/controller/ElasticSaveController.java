package com.zcw.cmall.search.controller;

import com.zcw.cmall.search.service.GoodSaveService;
import com.zcw.common.exception.ExceCodeEnum;
import com.zcw.common.to.es.SkuEsModel;
import com.zcw.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Chrisz
 * @date 2020/11/19 - 10:07
 */
@Slf4j
@RequestMapping("search/save")
@RestController
public class ElasticSaveController {

    @Autowired
    GoodSaveService goodSaveService;
    //上架商品
    @PostMapping("/good")
    public R goodStatusUp(@RequestBody List<SkuEsModel> skuEsModels){

        boolean b = false;
        try{
            b = goodSaveService.goodStatusUp(skuEsModels);
        }catch (Exception e){
            log.error("商品上架异常:{}",e);
            return R.error(ExceCodeEnum.GOODS_UP_EXCEPTION.getCode(),ExceCodeEnum.GOODS_UP_EXCEPTION.getMsg());
        }
        if (!b){
            return R.ok();
        }
        else{
            return R.error(ExceCodeEnum.GOODS_UP_EXCEPTION.getCode(),ExceCodeEnum.GOODS_UP_EXCEPTION.getMsg());
        }

    }
}
