package com.zcw.cmall.goods.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author Chrisz
 * @date 2020/12/2 - 20:24
 */
@ToString
@Data
public class SpuItemAttrGroupVo {
    private String groupName;
    private List<Attr> attrs;
}
