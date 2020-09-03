package com.dr.framework.core.orm.jdbc;

/**
 * 对应数据库列是否为空
 *
 * @author dr
 */
public enum TrueOrFalse {
    TRUE,
    FALSE,
    UN_KNOWN;

    public static TrueOrFalse from(String s) {
        if ("YES".equalsIgnoreCase(s)) {
            return TRUE;
        }
        if ("NO".equalsIgnoreCase(s)) {
            return FALSE;
        }
        return UN_KNOWN;
    }
}
