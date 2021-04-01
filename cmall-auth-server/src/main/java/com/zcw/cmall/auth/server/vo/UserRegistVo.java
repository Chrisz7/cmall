package com.zcw.cmall.auth.server.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;


import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * @author Chrisz
 * @date 2020/12/7 - 9:26
 */
@Data
public class UserRegistVo {

    @NotEmpty(message = "用户名必须填写")
    @Length(min = 6,max = 10,message = "用户名必须是6-10位字符")
    private String username;
    @NotEmpty(message = "密码必须填写")
    @Length(min = 6,max = 10,message = "密码必须是6-10位字符")
    private String password;
    @NotEmpty(message = "手机号必须填写")
    @Pattern(regexp = "^[1]([3-9])[0-9]{9}$",message = "手机号码格式不正确")
    private String phone;
    @NotEmpty(message = "验证码必须填写")
    private String code;
}
