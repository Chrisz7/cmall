package com.zcw.cmall.coupon.dao;

import com.zcw.cmall.coupon.entity.CouponHistoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券领取历史记录
 * 
 * @author Chrisz
 * @email sunlightcs@gmail.com
 * @date 2020-10-19 21:00:46
 */
@Mapper
public interface CouponHistoryDao extends BaseMapper<CouponHistoryEntity> {
	
}
