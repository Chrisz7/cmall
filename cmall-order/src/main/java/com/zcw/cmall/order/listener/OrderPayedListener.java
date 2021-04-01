package com.zcw.cmall.order.listener;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.zcw.cmall.order.config.AlipayTemplate;
import com.zcw.cmall.order.service.OrderService;
import com.zcw.cmall.order.vo.PayAsyncVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 接收支付宝的异步通知，要返回给支付宝success
 * @author Chrisz
 * @date 2021/1/2 - 11:47
 */
//这个地方得是RestController，要不然会发两次请求，并且第二次uri=/error
    //是用来返回数据的，不是页面跳转，
@RestController
public class OrderPayedListener {

    @Autowired
    AlipayTemplate alipayTemplate;
    @Autowired
    OrderService orderService;
    @PostMapping("/payed/notify")
    //支付宝传来的数据，自动封装成vo
    public String  handleAlipayed(PayAsyncVo vo ,HttpServletRequest request) throws UnsupportedEncodingException, AlipayApiException {

        //Map<String, String[]> parameterMap = request.getParameterMap();

        //验签
        //获取支付宝POST过来反馈信息
        Map<String,String> params = new HashMap<String,String>();
        Map<String,String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }

        boolean signVerified = AlipaySignature.rsaCheckV1(params, alipayTemplate.getAlipay_public_key(), alipayTemplate.getCharset(), alipayTemplate.getSign_type()); //调用SDK验证签名
        if (signVerified){
            //签名验证成功
            String result = orderService.handleAlipayed(vo);
            return result;
        }else {
            return "error";
        }


    }
}
