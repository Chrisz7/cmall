package com.zcw.cmall.goods.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 二级分类vo
 * @author Chrisz
 * @date 2020/11/20 - 11:27
 */
//无参构造器
@NoArgsConstructor
//全参构造器
@AllArgsConstructor
@Data
public class Catelog2Vo {

    private String catalog1Id;
    private List<Catelog3Vo> catalog3List;
    private String id;
    private String name;


    /**
     * 三级分类vo
     */
    //无参构造器
    @NoArgsConstructor
//全参构造器
    @AllArgsConstructor
    @Data
    public static class Catelog3Vo{
        private String catalog2Id;
        private String id;
        private String name;
    }
}
