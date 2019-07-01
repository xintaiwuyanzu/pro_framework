package com.dr.framework.common.entity;

/**
 * @author dr
 */
public interface StatusEntity<T> extends OrderEntity {
    public final Integer STATUS_ENABLE = 1;
    public final Integer STATUS_DISABLE = 0;
    public final Integer STATUS_UNKNOW = -1;
    public final String STATUS_ENABLE_STR = "1";
    public final String STATUS_DISABLE_STR = "0";
    public final String STATUS_UNKNOW_STR = "-1";

    public T getStatus();

    public void setStatus(T status);
}
