package com.zcw.cmall.search.service;

import com.zcw.cmall.search.vo.SearchParam;
import com.zcw.cmall.search.vo.SearchResponse;

/**
 * @author Chrisz
 * @date 2020/11/26 - 10:23
 */
public interface SearchService {
    /**
     * 检索
     * @param param 检索所有的条件
     * @return 返回检索的商品得结果
     */
    SearchResponse search(SearchParam param);
}
