package com.zcw.cmall.goods.web;

import com.zcw.cmall.goods.entity.CategoryEntity;
import com.zcw.cmall.goods.service.CategoryService;
import com.zcw.cmall.goods.vo.Catelog2Vo;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * @author Chrisz
 * @date 2020/11/19 - 21:05
 */
@Controller
public class IndexController {


    @Autowired
    RedissonClient redisson;
    /**
     * 首页
     */
    @Autowired
    CategoryService categoryService;
    @GetMapping({"/","/index.html"})
    public String indexPage(Model model){

        List<CategoryEntity> categoryEntities = categoryService.getLevel1Categories();
        model.addAttribute("categories",categoryEntities);
        return "index";
    }


    /**
     * 首页三级分类中的剩下二级分类
     * @return
     */
    //index/catalog.json
    //返回json数据，不是页面跳转
    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String, List<Catelog2Vo>> getCatalogJson(){
        Map<String, List<Catelog2Vo>> map = categoryService.getCatalogJson();
        return map;
    }

//    @GetMapping("/park")
//    @ResponseBody
//    public String park() throws InterruptedException {
//        RSemaphore park = redisson.getSemaphore("park");
//        park.acquire();
//        return "ok";
//    }
//    @GetMapping("/leave")
//    @ResponseBody
//    public String leave(){
//        RSemaphore park = redisson.getSemaphore("park");
//        park.release();
//        return "ok";
//    }
}
