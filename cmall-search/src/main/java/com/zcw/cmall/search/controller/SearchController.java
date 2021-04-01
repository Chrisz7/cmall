package com.zcw.cmall.search.controller;

import com.zcw.cmall.search.service.SearchService;
import com.zcw.cmall.search.vo.SearchParam;

import com.zcw.cmall.search.vo.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Chrisz
 * @date 2020/11/26 - 8:52
 */
@Controller
public class SearchController {

    @Autowired
    SearchService searchService;


    /**
     * 检索
     * @param param 检索所有的条件
     * @return      返回检索的商品得结果
     */
    //自动将页面提交过来的所有请求参数封装成指定的对象
    @GetMapping("/list.html")
    public String listPage(SearchParam param, Model model){

        SearchResponse response = searchService.search(param);

        model.addAttribute("response", response);
        //因为使用了thymeleaf，里面配置好了前缀，后缀
        return "search";
    }
}
