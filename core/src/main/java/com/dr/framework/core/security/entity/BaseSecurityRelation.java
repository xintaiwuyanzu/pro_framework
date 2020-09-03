package com.dr.framework.core.security.entity;

import com.dr.framework.common.entity.BaseStatusEntity;
import com.dr.framework.core.orm.annotations.Column;

/**
 * 简单抽象
 */
class BaseSecurityRelation extends BaseStatusEntity<Integer> {
    @Column(name = "security_code", length = 100, comment = "角色编码")
    private String code;
    @Column(length = 100, comment = "角色名称")
    private String name;
    @Column(length = 500, comment = "角色描述")
    private String description;
    @Column(name = "security_type", length = 50, comment = "权限类型")
    private String type;
    @Column(comment = "是否系统权限")
    private boolean isSys;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isSys() {
        return isSys;
    }

    public void setSys(boolean sys) {
        isSys = sys;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
