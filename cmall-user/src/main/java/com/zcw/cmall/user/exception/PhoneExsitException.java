package com.zcw.cmall.user.exception;

/**
 * @author Chrisz
 * @date 2020/12/9 - 9:20
 */
public class PhoneExsitException extends RuntimeException {

    public PhoneExsitException() {
        super("手机号已注册过");
    }
}
