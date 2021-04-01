package com.zcw.cmall.stock.dao;

import com.zcw.cmall.stock.entity.WareInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 仓库信息
 * 
 * @author Chrisz
 * @email sunlightcs@gmail.com
 * @date 2020-10-19 21:15:24
 */
@Mapper
public interface WareInfoDao extends BaseMapper<WareInfoEntity> {
	
}
