package com.zcw.cmall.user.dao;

import com.zcw.cmall.user.entity.GrowthChangeHistoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 成长值变化历史记录
 * 
 * @author Chrisz
 * @email sunlightcs@gmail.com
 * @date 2020-10-19 21:18:22
 */
@Mapper
public interface GrowthChangeHistoryDao extends BaseMapper<GrowthChangeHistoryEntity> {
	
}
