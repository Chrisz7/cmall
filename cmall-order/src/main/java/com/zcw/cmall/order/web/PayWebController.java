package com.zcw.cmall.order.web;

import com.alipay.api.AlipayApiException;
import com.zcw.cmall.order.config.AlipayTemplate;
import com.zcw.cmall.order.service.OrderService;
import com.zcw.cmall.order.vo.PayVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Chrisz
 * @date 2021/1/1 - 13:28
 */
@Controller
public class PayWebController {

    @Autowired
    AlipayTemplate alipayTemplate;
    @Autowired
    OrderService orderService;


    @ResponseBody
    @GetMapping(value = "/payOrder",produces = "text/html")
    public String payOrder(@RequestParam("orderSn") String orderSn) throws AlipayApiException {

        //获取当前订单的支付信息
        PayVo vo = orderService.getOrderPay(orderSn);
        //返回的是一个页面,将此页面直接交给浏览器渲染就行
        String pay = alipayTemplate.pay(vo);
        return pay;
    }
}
