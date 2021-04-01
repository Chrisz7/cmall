package com.zcw.cmall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.zcw.cmall.search.config.ElasticConfig;
import com.zcw.cmall.search.constant.EsConstant;
import com.zcw.cmall.search.service.SearchService;
import com.zcw.cmall.search.vo.SearchParam;
import com.zcw.cmall.search.vo.SearchResponse;
import com.zcw.common.to.es.SkuEsModel;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.lucene.search.join.ScoreMode.None;

/**
 * @author Chrisz
 * @date 2020/11/26 - 10:24
 */
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private RestHighLevelClient client;
    /**
     * 检索
     * @param param 检索所有的条件
     * @return      返回检索的商品得结果
     */
    @Override
    public SearchResponse search(SearchParam param) {

        //返回给页面的数据类型 SearchResponse
        SearchResponse response = null;
        //动态构建查询需要的DSL语句
        SearchRequest searchRequest = buildSearchRequest(param);
        try {
            //根据DSL语句执行查询，返回查询结果
            org.elasticsearch.action.search.SearchResponse searchResponse = client.search(searchRequest, ElasticConfig.COMMON_OPTIONS);

            //根据查询结果和页面传来的查询参数进行构建真正返回给页面的数据类型格式
            response = buildSearchResponse(searchResponse,param);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * 封装查询结果
     * @param searchResponse  param
     * @return
     */
    private SearchResponse buildSearchResponse(org.elasticsearch.action.search.SearchResponse searchResponse,SearchParam param) {

        SearchResponse response = new SearchResponse();
        SearchHits hits = searchResponse.getHits();
        //查询到的所有商品信息
        List<SkuEsModel> goods = new ArrayList<>();
        //List<SkuEsModel> goods = null; 不能这么写  可能会报空指针异常
        if(hits.getHits()!=null && hits.getHits().length>0) {
            for (SearchHit hit : hits.getHits()) {
                String source = hit.getSourceAsString();
                //使用alibaba.fastjson 将string类型的数据 转成 当时  存进ES中的对象 SkuEsModel
                SkuEsModel skuEsModel = JSON.parseObject(source, SkuEsModel.class);
                if(StringUtils.isNotEmpty(param.getKeyword())){
                    HighlightField skuTitle = hit.getHighlightFields().get("skuTitle");
                    String string = skuTitle.getFragments()[0].string();
                    skuEsModel.setSkuTitle(string);
                }
                goods.add(skuEsModel);
            }
        }
        response.setGoods(goods);
        //页码
        response.setPageNum(param.getPageNum());
        //总记录数
        long totalHits = hits.getTotalHits().value;
        response.setTotal(totalHits);
        int totalPages = (int)totalHits % EsConstant.GOODS_PAGE_SIZE == 0 ?
                (int)totalHits / EsConstant.GOODS_PAGE_SIZE : ((int)totalHits / EsConstant.GOODS_PAGE_SIZE) + 1;
        //总页码
        response.setTotalPages(totalPages);

        //导航页码
        List<Integer> pageNavs = new ArrayList<>();
        for (int i = 1; i <= totalPages; i++) {
            pageNavs.add(i);
        }
        response.setPageNavs(pageNavs);
        //聚合信息
        Aggregations aggregations = searchResponse.getAggregations();
        //品牌的聚合信息
        List<SearchResponse.BrandVo> brandVos = new ArrayList<>();
        ParsedLongTerms brand_agg = aggregations.get("brand_agg");
        for (Terms.Bucket bucket : brand_agg.getBuckets()) {
            SearchResponse.BrandVo brandVo = new SearchResponse.BrandVo();
            //ID
            brandVo.setBrandId(bucket.getKeyAsNumber().longValue());
            //brand_name_agg
            ParsedStringTerms brand_name_agg = bucket.getAggregations().get("brand_name_agg");
            String brand_name = brand_name_agg.getBuckets().get(0).getKeyAsString();
            brandVo.setBrandName(brand_name);
            //brand_img_agg
            ParsedStringTerms brand_img_agg = bucket.getAggregations().get("brand_img_agg");
            String brand_img = brand_img_agg.getBuckets().get(0).getKeyAsString();
            brandVo.setBrandImg(brand_img);
            brandVos.add(brandVo);
        }
        response.setBrands(brandVos);
        //分类的聚合信息
        ParsedLongTerms catalog_agg = aggregations.get("catalog_agg");
        List<? extends Terms.Bucket> buckets = catalog_agg.getBuckets();
        List<SearchResponse.CatalogVo> catalogVos = new ArrayList<>();
        for (Terms.Bucket bucket : buckets) {
            SearchResponse.CatalogVo catalogVo = new SearchResponse.CatalogVo();
            String keyAsString = bucket.getKeyAsString();
            //Long.parseLong() 将对象转成long ，这是Long中的方法
            catalogVo.setCatalogId(Long.parseLong(keyAsString));
            //catalog_name_agg
            ParsedStringTerms catalog_name_agg = bucket.getAggregations().get("catalog_name_agg");
            String catalog_name = catalog_name_agg.getBuckets().get(0).getKeyAsString();
            catalogVo.setCatalogName(catalog_name);
            catalogVos.add(catalogVo);
        }
        response.setCatalogs(catalogVos);

        //attr_agg 属性的聚合信息
        List<SearchResponse.AttrVo> attrVos = new ArrayList<>();
        ParsedNested attr_agg = aggregations.get("attr_agg");
        ParsedLongTerms attr_id_agg = attr_agg.getAggregations().get("attr_id_agg");
        for (Terms.Bucket bucket : attr_id_agg.getBuckets()) {
            SearchResponse.AttrVo attrVo = new SearchResponse.AttrVo();
            //id
            long attrId = bucket.getKeyAsNumber().longValue();
            attrVo.setAttrId(attrId);
            //name
            ParsedStringTerms attr_name_agg = bucket.getAggregations().get("attr_name_agg");
            String attrName = attr_name_agg.getBuckets().get(0).getKeyAsString();
            attrVo.setAttrName(attrName);
            //values
            ParsedStringTerms attr_value_agg = bucket.getAggregations().get("attr_value_agg");
            List<String> collect = attr_value_agg.getBuckets().stream().map(item -> {
                String string = ((Terms.Bucket) item).getKeyAsString();
                return string;
            }).collect(Collectors.toList());
            attrVo.setAttrValue(collect);
            attrVos.add(attrVo);
        }
        response.setAttrs(attrVos);
        return response;
    }

    /**
     * 构建DSL
     * @return
     */
    private SearchRequest buildSearchRequest(SearchParam param) {

        //SearchSourceBuilder用来构建DSL语句，放在SearchRequest中 searchRequest.source(searchSourceBuilder);
        // 再执行searchRequest  SearchResponse response = restHighLevelClient.search(searchRequest, ElasticConfig.COMMON_OPTIONS);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        /**
         * query
         */
        //第一层中的bool查询
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        if(!StringUtils.isEmpty(param.getKeyword())){
            //第一层bool中的must
            boolQuery.must(QueryBuilders.matchQuery("skuTitle",param.getKeyword()));
        }
        //第一层bool中的filter
        //Long类型的数据要判断不为空
        if (param.getCatalog3Id()!= null){
            //第一层bool中的filter中的第一个三级分类的term
            boolQuery.filter(QueryBuilders.termQuery("catalogId",param.getCatalog3Id()));
        }
        ////第一层bool中的filter中的第二个品牌id的多term
        //Long类型的List集合要判断不为空并且长度大于0
        if(param.getBrandId()!= null && param.getBrandId().size()>0){
            List<Long> brands = param.getBrandId();
            boolQuery.filter(QueryBuilders.termsQuery("brandId",brands));
        }
        //nested  attrs 属性
        if(param.getAttrs()!= null && param.getAttrs().size()>0){
            //增强for
            for (String attr : param.getAttrs()) {
                //2.nestedBoolQuery
                BoolQueryBuilder nestedBoolQuery = QueryBuilders.boolQuery();
                //约定传的值格式为"7_Apple;其他"
                String[] s = attr.split("_");
                String attrId = s[0];
                //值可能是多个
                String[] attrValues = s[1].split(";");
                //3.
                nestedBoolQuery.must(QueryBuilders.termQuery("attrs.attrId",attrId));
                nestedBoolQuery.must(QueryBuilders.termsQuery("attrs.attrValue",attrValues));
                //1.nestedQuery
                NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("attrs", nestedBoolQuery, None);
                boolQuery.filter(nestedQuery);
            }

        }
        //hasStock  hasStock默认初始值为1，没啥作用，页面传来的值param.getHasStock()==1就是true有货查有货，无货查无货
        boolQuery.filter(QueryBuilders.termQuery("hasStock",param.getHasStock()==1));
        //range  skuPrice
        if(!StringUtils.isEmpty(param.getSkuPrice())){
            //2.
            RangeQueryBuilder skuPriceRange = QueryBuilders.rangeQuery("skuPrice");
            //split分割  约定传来的数据格式 "_6000" "6000_" "6000_8000"
            String[] s = param.getSkuPrice().split("_");
            if (s.length == 2){
                //gte大于 lte小于
                skuPriceRange.gte(s[0]).lte(s[1]);
            }else if(s.length == 1){
                //startsWith以什么开始
                if(param.getSkuPrice().startsWith("_")){
                    skuPriceRange.lte(s[0]);
                }else {
                    skuPriceRange.gte(s[0]);
                }
            }
            //1.
            boolQuery.filter(skuPriceRange);
        }
        //第一层query
        searchSourceBuilder.query(boolQuery);

        /**
         * sort 排序只会根据一种规则排
         */
        //为什么不用isNotEmpty
        //isBlank和isEmpty的区别
        if(StringUtils.isNotEmpty(param.getSort())){
            String sort = param.getSort();
            String[] s = sort.split("_");
            //忽略大小写判断相等
            SortOrder order = s[1].equalsIgnoreCase("asc") ? SortOrder.ASC : SortOrder.DESC;
            searchSourceBuilder.sort(s[0],order);
        }

        /**
         *分页
         */
        searchSourceBuilder.from((param.getPageNum()-1)*EsConstant.GOODS_PAGE_SIZE);
        searchSourceBuilder.size(EsConstant.GOODS_PAGE_SIZE);

        /**
         * 高亮
         */
        if (StringUtils.isNotEmpty(param.getKeyword())){
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle");
            highlightBuilder.preTags("<b style='color:red'>");
            highlightBuilder.postTags("</b>");
            searchSourceBuilder.highlighter(highlightBuilder);
        }

        System.out.println(searchSourceBuilder.toString());
        /**
         * 聚合分析
         */
        //brand_agg
        TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg");
        //从查询到的结果拿50个显示
        brand_agg.field("brandId").size(50);
        //brand_agg中的子聚合
        //brand_name_agg
        TermsAggregationBuilder brand_name_agg = AggregationBuilders.terms("brand_name_agg").field("brandName").size(50);

        brand_agg.subAggregation(brand_name_agg);
        TermsAggregationBuilder brand_img_agg = AggregationBuilders.terms("brand_img_agg").field("brandImg").size(50);

        brand_agg.subAggregation(brand_img_agg);
        searchSourceBuilder.aggregation(brand_agg);

        //catalog_agg
        TermsAggregationBuilder catalog_agg = AggregationBuilders.terms("catalog_agg");
        catalog_agg.field("catalogId").size(50);
        //catalog_name_agg
        TermsAggregationBuilder catalogNameAgg = AggregationBuilders.terms("catalog_name_agg").field("catalogName").size(50);
        catalog_agg.subAggregation(catalogNameAgg);
        searchSourceBuilder.aggregation(catalog_agg);

        //attr_agg
        NestedAggregationBuilder nestedAggregationBuilder = AggregationBuilders.nested("attr_agg", "attrs");
        //attr_agg的子聚合attr_id_agg
        TermsAggregationBuilder attr_id_agg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId").size(50);
        //attr_id_agg的子聚合attr_name_agg attr_value_agg
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(50));
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(50));
        nestedAggregationBuilder.subAggregation(attr_id_agg);
        searchSourceBuilder.aggregation(nestedAggregationBuilder);

        System.out.println(searchSourceBuilder.toString());
        SearchRequest searchRequest = new SearchRequest(new String[]{EsConstant.GOODS_INDEX},  searchSourceBuilder);

        return searchRequest;
    }
}
