package com.dr.framework.common.entity;

import com.dr.framework.core.orm.annotations.Column;

/**
 * 带有描述信息的常用实体类
 *
 * @author dr
 */
public class BaseDescriptionEntity<T> extends BaseStatusEntity<T> implements DescriptionEntity<T> {
    @Column(name = "pName", comment = "名称", length = 500, order = 8)
    private String name;
    @Column(name = "pCode", comment = "编码", length = 500, order = 9)
    private String code;
    @Column(name = "pType", comment = "类型", length = 500, order = 10)
    private String type;
    @Column(name = "pDescription", comment = "描述", length = 1000, order = 11)
    private String description;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }
}
