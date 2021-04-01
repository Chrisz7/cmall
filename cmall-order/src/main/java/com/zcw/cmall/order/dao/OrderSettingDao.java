package com.zcw.cmall.order.dao;

import com.zcw.cmall.order.entity.OrderSettingEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单配置信息
 * 
 * @author Chrisz
 * @email sunlightcs@gmail.com
 * @date 2020-10-19 21:11:10
 */
@Mapper
public interface OrderSettingDao extends BaseMapper<OrderSettingEntity> {
	
}
