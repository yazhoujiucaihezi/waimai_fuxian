package com.sky.exception;

/**
 * 密码错误异常
 */
public class PasswordErrorException extends BaseException {

    public PasswordErrorException() {
        super("密码错误");
    }

    public PasswordErrorException(String msg) {
        super(msg);
    }

}
