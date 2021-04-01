package com.zcw.cmall.gateway.config;


import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.fastjson.JSON;
import com.zcw.common.exception.ExceCodeEnum;
import com.zcw.common.utils.R;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


/**
 * @author Chrisz
 * @date 2021/1/4 - 14:28
 */
@Configuration
//sentinel提供了web
public class GatewaySentinelConfig {

    public GatewaySentinelConfig(){
        //
        GatewayCallbackManager.setBlockHandler(new BlockRequestHandler() {
            //网关限流了,调用这个回调
            @Override
            public Mono<ServerResponse> handleRequest(ServerWebExchange serverWebExchange, Throwable throwable) {
                R error = R.error(ExceCodeEnum.TO_MANY_REQUEST.getCode(), ExceCodeEnum.TO_MANY_REQUEST.getMsg());
                String errorJson = JSON.toJSONString(error);

                Mono<ServerResponse> body = ServerResponse.ok().body(Mono.just(errorJson), String.class);
                return body;
            }
        });
    }
}
