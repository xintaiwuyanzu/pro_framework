package com.dr.framework.sys.service;

import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.Table;
import com.dr.framework.core.util.Constants;

/**
 * @author dr
 */
@Table(name = Constants.SYS_TABLE_PREFIX + "role_permission"
        , comment = "角色权限关联表"
        , module = Constants.SYS_MODULE_NAME)
class EntityRolePermission extends EntityAbstractRoleRelation {
    @Column(length = 50, comment = "权限id", name = "permission_id")
    private String permissionId;

    public String getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(String permissionId) {
        this.permissionId = permissionId;
    }
}
