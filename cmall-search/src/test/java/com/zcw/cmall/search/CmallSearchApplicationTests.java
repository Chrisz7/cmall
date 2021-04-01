package com.zcw.cmall.search;

import com.alibaba.fastjson.JSON;
import com.zcw.cmall.search.config.ElasticConfig;
import lombok.Data;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
class CmallSearchApplicationTests {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /***
     * 查询
     * @throws IOException
     */
    @Test
    void searchData() throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("bank");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("address","mill"));
        TermsAggregationBuilder ageAgg = AggregationBuilders.terms("ageAgg").field("age").size(10);
        searchSourceBuilder.aggregation(ageAgg);
        AvgAggregationBuilder balanceAgg = AggregationBuilders.avg("balanceAvg").field("balance");
        searchSourceBuilder.aggregation(balanceAgg);
        System.out.println(searchSourceBuilder.toString());
//        searchSourceBuilder.size();
//        searchSourceBuilder.from();
//        searchSourceBuilder.aggregation();
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = restHighLevelClient.search(searchRequest, ElasticConfig.COMMON_OPTIONS);
        System.out.println(response);
    }

    /**
     * 给es存储数据
     */
    @Test
    void indexData() throws IOException {
        //IndexRequest request = new IndexRequest("users").id("1").source("username","Chrisz","age","20");
        User user = new User();
        user.setAge(20);
        user.setUserName("Chrisz");
        String json =  JSON.toJSONString(user);
        IndexRequest request = new IndexRequest("users").id("1").source(json, XContentType.JSON);
        IndexResponse response = restHighLevelClient.index(request, ElasticConfig.COMMON_OPTIONS);
        System.out.println(response);
    }
    @Data
    class User{
        private String userName;
        private Integer age;
    }
    @Test
    void contextLoads() {
        System.out.println(restHighLevelClient);
    }

}
