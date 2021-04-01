package com.zcw.cmall.goods.dao;

import com.zcw.cmall.goods.entity.AttrEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品属性
 *
 * @author Chrisz
 * @email sunlightcs@gmail.com
 * @date 2020-10-19 15:27:36
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {

    //先生成一个Param
    //在生成statement
    List<Long> selectSearchAttrs(@Param("ids") List<Long> ids);
}
