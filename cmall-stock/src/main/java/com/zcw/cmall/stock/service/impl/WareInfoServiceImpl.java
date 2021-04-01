package com.zcw.cmall.stock.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.zcw.cmall.stock.feign.UserFeignService;
import com.zcw.cmall.stock.vo.FareVo;
import com.zcw.cmall.stock.vo.MemberAddressVo;
import com.zcw.common.utils.R;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcw.common.utils.PageUtils;
import com.zcw.common.utils.Query;

import com.zcw.cmall.stock.dao.WareInfoDao;
import com.zcw.cmall.stock.entity.WareInfoEntity;
import com.zcw.cmall.stock.service.WareInfoService;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Autowired
    UserFeignService userFeignService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareInfoEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            wrapper.eq("id",key)
                    .or().like("name",key)
                    .or().like("address",key)
                    .or().like("areacode",key);
        }

        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    /**
     * 获取运费信息
     * @param addrId
     * @return
     */
    @Override
    public FareVo getFare(Long addrId) {
        FareVo fareVo = new FareVo();
        R r = userFeignService.addrInfo(addrId);
        MemberAddressVo data = r.getData("memberReceiveAddress",new TypeReference<MemberAddressVo>() {});
        if (data!=null){
            String phone = data.getPhone();
            String substring = phone.substring(phone.length() - 1, phone.length());
            BigDecimal fare = new BigDecimal(substring);
            fareVo.setAddress(data);
            fareVo.setFare(fare);
            return fareVo;
        }
        return null;
    }

}
