package com.zcw.cmall.search.vo;

import com.zcw.common.to.es.SkuEsModel;
import lombok.Data;

import java.util.List;

/**
 * @author Chrisz
 * @date 2020/11/26 - 15:44
 */
@Data
public class SearchResponse {

    /**
     * 商品信息 SkuEsModel
     */
    private List<SkuEsModel> goods; //查询到的所有商品信息

    /**
     * 品牌信息 BrandVo
     */
    private List<BrandVo> brands;//当前查询涉及到的所有品牌

    /**
     * 分类信息 CatalogVo
     */
    private List<CatalogVo> catalogs;//当前查询涉及到的所有分类

    /**
     * 属性信息 attrVo
     */
    private List<AttrVo> attrs;//所有涉及到的属性

    /**
     * 分页信息
     */
    private Integer pageNum;//页码
    private Long total;//总记录数
    private Integer totalPages;//总页数
    private List<Integer> pageNavs;//导航页码

    //==========以上是返回给页面的数据============

    /**
     * 品牌信息 BrandVo
     */
    //静态内部类
    @Data
    public static class BrandVo{
        private Long brandId;//品牌Id
        private String brandName;//品牌名称
        private String brandImg;//品牌图片
    }

    /**
     * 分类信息 catalogVo
     */
    @Data
    public static class CatalogVo{
        private Long catalogId;//分类Id
        private String catalogName;//分类名称
    }

    /**
     * 属性信息 attrVo
     */
    @Data
    public static class AttrVo{
        private Long attrId;//属性Id
        private String attrName;//属性名称
        private List<String> attrValue;//所有涉及到的属性值
    }


}
