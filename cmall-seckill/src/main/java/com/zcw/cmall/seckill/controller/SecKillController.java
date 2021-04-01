//package com.zcw.cmall.seckill.controller;
//
//
//import com.zcw.cmall.seckill.service.SeckillService;
//import com.zcw.cmall.seckill.to.SeckillSkuRedisTo;
//import com.zcw.common.utils.R;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//public class SeckillController {
//
//    @Autowired
//    private SeckillService secKillService;
//
//    /**
//     * 当前时间可以参与秒杀的商品信息
//     * @return
//     */
//    @GetMapping(value = "/getCurrentSeckillSkus")
//    @ResponseBody
//    public R getCurrentSeckillSkus() {
//        //获取到当前可以参加秒杀商品的信息
//        List<SeckillSkuRedisTo> vos = secKillService.getCurrentSeckillSkus();
//
//        return R.ok().setData(vos);
//    }
//
//    @ResponseBody
//    @GetMapping(value = "/getSeckillSkuInfo/{skuId}")
//    public R getSeckillSkuInfo(@PathVariable("skuId") Long skuId) {
//        //SeckillSkuRedisTo to = secKillService.getSeckillSkuInfo(skuId);
//        return R.ok().setData(to);
//    }
//
//
//    @GetMapping("/kill")
//    public String kill(@RequestParam("killId") String killId,
//                       @RequestParam("key")String key,
//                       @RequestParam("num")Integer num,
//                       Model model) {
//        String orderSn= null;
//        try {
//            //orderSn = secKillService.kill(killId, key, num);
//            model.addAttribute("orderSn", orderSn);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        return "success";
//    }
//
//
//}
