package com.zcw.cmall.goods.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author Chrisz
 * @date 2020/11/1 - 10:13
 */
@Data
public class AttrRespVo extends AttrVo {


    private String catelogName;

    private String groupName;

    private Long[] catelogPath;
}
