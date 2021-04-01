package com.zcw.cmall.goods.dao;

import com.zcw.cmall.goods.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author Chrisz
 * @email sunlightcs@gmail.com
 * @date 2020-10-19 15:27:36
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
