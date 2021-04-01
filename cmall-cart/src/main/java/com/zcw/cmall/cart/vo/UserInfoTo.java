package com.zcw.cmall.cart.vo;

import lombok.Data;
import lombok.ToString;

/**
 * @author Chrisz
 * @date 2020/12/18 - 9:47
 *
 * TO 数据传输对象
 */
//打印使用的注解
@ToString
@Data
public class UserInfoTo {

    private Long userId;
    private String userKey;

    private boolean tempUser = false;//标识临时用户
}
