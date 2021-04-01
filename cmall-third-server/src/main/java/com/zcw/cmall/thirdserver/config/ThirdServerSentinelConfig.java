package com.zcw.cmall.thirdserver.config;

import com.alibaba.csp.sentinel.adapter.servlet.callback.UrlBlockHandler;
import com.alibaba.csp.sentinel.adapter.servlet.callback.WebCallbackManager;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.zcw.common.exception.ExceCodeEnum;
import com.zcw.common.utils.R;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Chrisz
 * @date 2021/1/4 - 14:28
 */
@Configuration
//sentinel提供了web
public class ThirdServerSentinelConfig {

    public ThirdServerSentinelConfig(){
        //被限制了怎么办
        WebCallbackManager.setUrlBlockHandler(new UrlBlockHandler(){
            @Override
            public void blocked(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, BlockException e) throws IOException {
                R error = R.error(ExceCodeEnum.TO_MANY_REQUEST.getCode(), ExceCodeEnum.TO_MANY_REQUEST.getMsg());
                //类型编码
                httpServletResponse.setCharacterEncoding("UTF-8");
                //内容类型
                httpServletResponse.setContentType("application/json");
                httpServletResponse.getWriter().write(JSON.toJSONString(error));
            }
        });
    }
}
