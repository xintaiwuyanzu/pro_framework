package com.dr.framework.common.entity;

/**
 * 树状结构实体类
 *
 * @param <T>
 * @author dr
 */
public interface TreeEntity<T> extends StatusEntity<T> {
    /**
     * 获取父id
     *
     * @return
     */
    String getParentId();

    /**
     * 设置父id
     *
     * @param parentId
     */
    void setParentId(String parentId);

}
