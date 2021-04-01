package com.zcw.common.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Chrisz
 * @date 2020/12/11 - 19:45
 */
@Data
//MemberRespVo默认使用的是jdk序列化，implements Serializable 只有序列化成二进制流或串才能远程存进redis
public class MemberRespVo implements Serializable {

    /**
     * id
     */

    private Long id;
    /**
     * 会员等级id
     */
    private Long levelId;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 手机号码
     */
    private String mobile;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 头像
     */
    private String header;
    /**
     * 性别
     */
    private Integer gender;
    /**
     * 生日
     */
    private Date birth;
    /**
     * 所在城市
     */
    private String city;
    /**
     * 职业
     */
    private String job;
    /**
     * 个性签名
     */
    private String sign;
    /**
     * 用户来源
     */
    private Integer sourceType;
    /**
     * 积分
     */
    private Integer integration;
    /**
     * 成长值
     */
    private Integer growth;
    /**
     * 启用状态
     */
    private Integer status;
    /**
     * 注册时间
     */
    private Date createTime;

    private String socialUid;
    private String accessToken;
    private Long expiresIn;
}
