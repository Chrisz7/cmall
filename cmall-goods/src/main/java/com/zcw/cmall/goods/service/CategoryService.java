package com.zcw.cmall.goods.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zcw.cmall.goods.vo.Catelog2Vo;
import com.zcw.common.utils.PageUtils;
import com.zcw.cmall.goods.entity.CategoryEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author Chrisz
 * @email sunlightcs@gmail.com
 * @date 2020-10-19 15:27:36
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryEntity> listWithTree();

    void removeMenuByIds(List<Long> asList);

    Long[] findCatelogPath(Long catelogId);

    void updateCascade(CategoryEntity category);

    /**
     * 一进到首页查一级分类
     */
    List<CategoryEntity> getLevel1Categories();

    /**
     *首页三级分类中的剩下二级分类
     * @return
     */
    Map<String, List<Catelog2Vo>> getCatalogJson();

}

