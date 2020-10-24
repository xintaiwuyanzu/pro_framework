package com.dr.framework.sys.service;

import com.dr.framework.core.orm.annotations.Column;

/**
 * 简单抽象角色关联表
 *
 * @author dr
 */
class EntityAbstractRoleRelation {
    @Column(length = 50, comment = "角色Id")
    private String roleId;

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }
}
