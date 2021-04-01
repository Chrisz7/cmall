package com.zcw.cmall.user.exception;

/**
 * @author Chrisz
 * @date 2020/12/9 - 9:20
 */
public class UsernameExsitException extends RuntimeException {

    public UsernameExsitException() {
        super("用户名已注册过");
    }
}
