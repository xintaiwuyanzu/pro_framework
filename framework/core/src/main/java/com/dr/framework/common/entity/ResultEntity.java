package com.dr.framework.common.entity;

import org.springframework.util.StringUtils;

/**
 * 用来封装返回数据的
 *
 * @author dr
 */
public class ResultEntity<T> {
    /**
     * 200	请求成功
     * 400	缺少请求参数
     * 401	token校验失败，非法请求
     * 403	用户未登录
     * 500	服务器内部错误
     */
    private String code;
    private String message;
    private boolean success;
    private T data;

    public static ResultEntity success() {
        return new ResultEntity(true, null, null);
    }

    public static <T> ResultEntity success(T data) {
        return new ResultEntity<T>(true, null, data);
    }


    public static <T> ResultEntity success(String message, T data) {
        return new ResultEntity<T>(true, message, data);
    }

    public static ResultEntity error(String msg) {
        return new ResultEntity(false, msg, null);
    }

    public static <T> ResultEntity error(T data) {
        return new ResultEntity<T>(false, null, data);
    }

    public static <T> ResultEntity error(String message, T data) {
        return new ResultEntity<T>(false, message, data);
    }

    public static <T> ResultEntity error(String message, String code, T data) {
        return new ResultEntity<T>(false, message, code, data);
    }


    public ResultEntity(boolean success, String message, T data) {
        this.success = success;
        if (success) {
            this.code = "200";
        } else {
            this.code = "500";
        }
        if (StringUtils.isEmpty(message)) {
            if (success) {
                this.message = "请求成功！";
            } else {
                this.code = "请求失败，服务器内部错误！";
            }
        } else {
            this.message = message;
        }
        this.data = data;
    }

    public ResultEntity(boolean success, String message, String code, T data) {
        this.code = code;
        this.message = message;
        this.success = success;
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


}
