package com.zcw.cmall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.zcw.cmall.search.config.ElasticConfig;
import com.zcw.cmall.search.constant.EsConstant;
import com.zcw.cmall.search.service.GoodSaveService;
import com.zcw.common.to.es.SkuEsModel;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Chrisz
 * @date 2020/11/19 - 10:12
 */
@Slf4j
@Service
public class GoodSaveServiceImpl implements GoodSaveService {


    @Autowired
    RestHighLevelClient restHighLevelClient;
    /**
     * 商品上架
     * @param skuEsModels
     */
    @Override
    public boolean goodStatusUp(List<SkuEsModel> skuEsModels) throws IOException {

        //保存到Es
        //给Es建立索引  goods
        BulkRequest bulkRequest = new BulkRequest();
        for (SkuEsModel skuEsModel : skuEsModels) {
            IndexRequest indexRequest = new IndexRequest(EsConstant.GOODS_INDEX);
            indexRequest.id(skuEsModel.getSkuId().toString());
            String s = JSON.toJSONString(skuEsModel);
            indexRequest.source(s, XContentType.JSON);
            bulkRequest.add(indexRequest);

        }
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, ElasticConfig.COMMON_OPTIONS);
        boolean b = bulk.hasFailures();
        List<String> collect = Arrays.stream(bulk.getItems()).map(item -> {
            return item.getId();
        }).collect(Collectors.toList());
        log.info("商品上架成功:{}",collect);

        return b;
    }
}
