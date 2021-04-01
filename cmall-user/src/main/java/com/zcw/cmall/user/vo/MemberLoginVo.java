package com.zcw.cmall.user.vo;

import lombok.Data;

/**
 * @author Chrisz
 * @date 2020/12/10 - 8:53
 */
@Data
public class MemberLoginVo {


    private String loginacct;//登录使用用户名或手机号
    private String password;
}
