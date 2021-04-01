package com.zcw.cmall.goods.feign.fallback;

import com.zcw.cmall.goods.feign.SearchFeignService;
import com.zcw.common.exception.ExceCodeEnum;
import com.zcw.common.to.es.SkuEsModel;
import com.zcw.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Chrisz
 * @date 2021/1/6 - 16:38
 */
@Slf4j
@Component
public class SearchFeignServiceFallBack implements SearchFeignService {
    @Override
    public R goodStatusUp(List<SkuEsModel> skuEsModels) {
        log.info("熔断....");
        return R.error(ExceCodeEnum.TO_MANY_REQUEST.getCode(),ExceCodeEnum.TO_MANY_REQUEST.getMsg());
    }
}
