package com.dr.framework.common.entity;

import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.Id;

/**
 * 实体类基类，只有主键字段
 *
 * @author dr
 */
public class BaseEntity implements IdEntity {
    @Id
    @Column(name = "id", comment = "主建", length = 100, order = 1)
    private String id;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }
}
