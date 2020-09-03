package com.dr.framework.common.entity;

/**
 * 带有主键的实体类
 *
 * @author dr
 */
public interface IdEntity {
    String MULTI_STR_SPLIT_CHAR = ";";
    String ID_COLUMN_NAME = "id";

    /**
     * 获取id的值
     *
     * @return
     */
    String getId();

    /**
     * 设置主键
     *
     * @param id
     */
    void setId(String id);
}
