package com.zcw.cmall.goods.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zcw.cmall.goods.entity.BrandEntity;
import com.zcw.common.utils.PageUtils;
import com.zcw.cmall.goods.entity.CategoryBrandRelationEntity;
import com.zcw.common.valid.AddGroup;
import com.zcw.common.valid.UpdateGroup;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.List;
import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author Chrisz
 * @email sunlightcs@gmail.com
 * @date 2020-10-19 15:27:36
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveDetail(CategoryBrandRelationEntity categoryBrandRelation);

    void updateBrand(Long brandId, String name);


    void updateCategory(Long catId, String name);

    List<BrandEntity> getBrandsByCatId(Long catId);
}

