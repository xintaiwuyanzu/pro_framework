package com.dr.framework.common.entity;

import com.dr.framework.core.orm.annotations.Column;

/**
 * 包含有父id的实体类
 *
 * @author dr
 */
public class BaseTreeEntity<T> extends BaseStatusEntity<T> implements TreeEntity<T> {

    @Column(name = "parent_id", comment = "父Id", length = 100, order = 8)
    private String parentId;

    @Override
    public String getParentId() {
        return parentId;
    }

    @Override
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}
