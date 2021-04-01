package com.zcw.cmall.search.vo;

import lombok.Data;

import java.util.List;

/**
 * 封装首页传递过来的所有查询条件
 * @author Chrisz
 * @date 2020/11/26 - 10:21
 */
@Data
public class SearchParam {

    private String keyword;//全文匹配关键字
    private Long catalog3Id;//三级分类id
    private String sort;//排序条件
    private Integer hasStock = 1;//是否有货 默认有库存
    private String skuPrice;//价格区间
    private List<Long> brandId;//品牌
    private List<String> attrs;//商品属性
    private Integer pageNum = 1;//页码是浏览到了多少页，总页数京东会固定为100页
}
