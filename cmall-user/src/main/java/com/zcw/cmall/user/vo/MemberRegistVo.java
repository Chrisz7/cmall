package com.zcw.cmall.user.vo;

import lombok.Data;


import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * @author Chrisz
 * @date 2020/12/9 - 9:03
 */
@Data
public class MemberRegistVo {


    private String username;

    private String password;

    private String phone;
}
