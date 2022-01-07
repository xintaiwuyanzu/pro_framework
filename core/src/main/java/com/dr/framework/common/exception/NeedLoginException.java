package com.dr.framework.common.exception;

/**
 * 用户未登录异常
 *
 * @author dr
 */
public class NeedLoginException extends RuntimeException {
    public NeedLoginException(String message) {
        super(message);
    }
}
