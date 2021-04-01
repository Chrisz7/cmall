package com.zcw.cmall.goods.feign.fallback;

import com.zcw.cmall.goods.feign.StockFeignService;
import com.zcw.common.exception.ExceCodeEnum;
import com.zcw.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Chrisz
 * @date 2021/1/6 - 18:29
 */
@Slf4j
@Component
public class StockFeignServiceFallBack implements StockFeignService {
    @Override
    public R getSkusHasStock(List<Long> skuIds) {
        log.info("熔断....");
        return R.error(ExceCodeEnum.TO_MANY_REQUEST.getCode(),ExceCodeEnum.TO_MANY_REQUEST.getMsg());
    }
}
