package com.zcw.cmall.auth.server.vo;

import lombok.Data;

/**
 * @author Chrisz
 * @date 2020/12/11 - 18:51
 */
@Data
public class SocialUser {

    private String access_token;
    private String remind_in;
    private long expires_in;
    private String uid;
    private String isRealName;
}
