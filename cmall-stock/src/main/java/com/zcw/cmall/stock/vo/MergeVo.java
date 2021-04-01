package com.zcw.cmall.stock.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Chrisz
 * @date 2020/11/11 - 10:33
 */
@Data
public class MergeVo {
    //使用包装类型的，不提交的话，就要封装空的值
    private Long purchaseId;
    private List<Long> items;
}
