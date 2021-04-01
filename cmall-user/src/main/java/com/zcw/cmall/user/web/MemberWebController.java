package com.zcw.cmall.user.web;

import com.zcw.cmall.user.feign.OrderFeignService;
import com.zcw.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;

/**
 * @author Chrisz
 * @date 2021/1/1 - 20:53
 */
@Controller
public class MemberWebController {

    @Autowired
    OrderFeignService orderFeignService;

    @GetMapping("/memberOrder.html")
    public String memberOrderPage(@RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
                                  Model model){

        //查出登录用户的所有订单列表数据
        HashMap<String, Object> page = new HashMap<>();
        page.put("page",pageNum.toString());
        R r = orderFeignService.listWithItem(page);
        model.addAttribute("orders",r);
        return "orderList";
    }
}
