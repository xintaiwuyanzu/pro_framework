package com.dr.framework.common.entity;

/**
 * @author dr
 */
public interface StatusEntity<T> extends OrderEntity {
    String STATUS_COLUMN_KEY = "status_info";

    Integer STATUS_ENABLE = 1;
    Integer STATUS_DISABLE = 0;
    Integer STATUS_UNKNOW = -1;
    String STATUS_ENABLE_STR = "1";
    String STATUS_DISABLE_STR = "0";
    String STATUS_UNKNOW_STR = "-1";

    T getStatus();

    void setStatus(T status);
}
