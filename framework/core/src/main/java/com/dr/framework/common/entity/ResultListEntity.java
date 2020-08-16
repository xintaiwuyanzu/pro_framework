package com.dr.framework.common.entity;

import java.util.List;

/**
 * 用来封装返回数据的
 *
 * @author dr
 */
public class ResultListEntity<T> extends ResultEntity<List<T>> {

    public static ResultListEntity success() {
        return new ResultListEntity(true, null, null);
    }

    public static <T> ResultListEntity<T> success(List<T> data) {
        return new ResultListEntity<>(true, null, data);
    }

    public static <T> ResultListEntity<T> success(String message, List<T> data) {
        return new ResultListEntity<>(true, message, data);
    }

    public static ResultListEntity error(String msg) {
        return new ResultListEntity(false, msg, null);
    }

    public static <T> ResultListEntity<T> error(List<T> data) {
        return new ResultListEntity<>(false, null, data);
    }

    public static <T> ResultListEntity<T> error(String message, List<T> data) {
        return new ResultListEntity<>(false, message, data);
    }

    public static <T> ResultListEntity<T> error(String message, String code, List<T> data) {
        return new ResultListEntity<>(false, message, code, data);
    }

    public ResultListEntity(boolean success, String message, List<T> data) {
        super(success, message, data);
    }

    public ResultListEntity(boolean success, String message, String code, List<T> data) {
        super(success, message, code, data);
    }

}
