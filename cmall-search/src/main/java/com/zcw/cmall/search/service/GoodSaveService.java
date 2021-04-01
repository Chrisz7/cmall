package com.zcw.cmall.search.service;

import com.zcw.common.to.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

/**
 * @author Chrisz
 * @date 2020/11/19 - 10:10
 */
public interface GoodSaveService {
    /**
     * 商品上架
     * @param skuEsModels
     */
    boolean goodStatusUp(List<SkuEsModel> skuEsModels) throws IOException;
}
