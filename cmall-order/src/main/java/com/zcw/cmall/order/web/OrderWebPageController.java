package com.zcw.cmall.order.web;

import com.zcw.cmall.order.service.OrderService;
import com.zcw.cmall.order.vo.OrderConfirmVo;
import com.zcw.cmall.order.vo.OrderSubmitRespVo;
import com.zcw.cmall.order.vo.OrderSubmitVo;
import com.zcw.common.exception.NoStockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.concurrent.ExecutionException;

/**
 * @author Chrisz
 * @date 2020/12/22 - 15:44
 */
@Controller
public class OrderWebPageController {

    @Autowired
    OrderService orderService;

    /**
     * 订单服务的各种页面
     *
     * @param page
     * @return
     */
    @GetMapping("/{page}.html")
    public String OrderWebPage(@PathVariable("page") String page) {

        return page;
    }

    /**
     * 订单确认页
     *
     * @return
     */
    @GetMapping("/toTrade")
    public String toTrade(Model model) throws ExecutionException, InterruptedException {

        OrderConfirmVo vo = orderService.confirmOrder();
        model.addAttribute("orderConfirmData", vo);
        return "confirm";
    }

    /**
     * 下单
     *
     * @param vo
     * @return
     */
    @PostMapping("/submitOrder")
    //这个地方传的也不是json 提交过来的是与OrderSubmitVo对应的表单
    public String submitOrder(OrderSubmitVo vo, Model model, RedirectAttributes redirectAttributes) {

        try {
            //ctrl alt T
            //System.out.println(vo);
            OrderSubmitRespVo orderSubmitRespVo = orderService.submitOrder(vo);
            if (orderSubmitRespVo.getCode() == 0) {
                //成功
                model.addAttribute("orderSubmitRespVo", orderSubmitRespVo);
                return "pay";
            } else {
                //失败
                String msg = "下单失败:";
                switch (orderSubmitRespVo.getCode()) {
                    case 1:
                        msg += "订单过期,刷新再次提交";
                        break;
                    case 2:
                        msg += "订单商品价格发生变化,确认后再次提交";
                        break;
                    case 3:
                        msg += "库存锁定失败,商品库存不足";
                        break;
                }
                //TODO 往session中放
                redirectAttributes.addFlashAttribute("msg", msg);
                return "redirect:http://order.cmall.com/toTrade";
            }
        } catch (Exception e) {
            //e instanceof NoStockException 判断异常是不是这个异常
            if (e instanceof NoStockException) {
                //((NoStockException) e).getMessage();
                String msg = e.getMessage();
                redirectAttributes.addFlashAttribute("msg", msg);
            }
            return "redirect:http://order.cmall.com/toTrade";
        }
    }
}
