package com.zcw.cmall.coupon.dao;

import com.zcw.cmall.coupon.entity.MemberPriceEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品会员价格
 * 
 * @author Chrisz
 * @email sunlightcs@gmail.com
 * @date 2020-10-19 21:00:46
 */
@Mapper
public interface MemberPriceDao extends BaseMapper<MemberPriceEntity> {
	
}
