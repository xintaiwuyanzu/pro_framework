package com.dr.framework.common.entity;

/**
 * @author dr
 */
public interface StatusEntity<T> extends OrderEntity {
    final Integer STATUS_ENABLE = 1;
    final Integer STATUS_DISABLE = 0;
    final Integer STATUS_UNKNOW = -1;
    final String STATUS_ENABLE_STR = "1";
    final String STATUS_DISABLE_STR = "0";
    final String STATUS_UNKNOW_STR = "-1";

    T getStatus();

    void setStatus(T status);
}
