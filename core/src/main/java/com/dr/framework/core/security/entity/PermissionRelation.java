package com.dr.framework.core.security.entity;

import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.Table;
import com.dr.framework.core.util.Constants;

/**
 * 权限关联表
 *
 * @author dr
 */
@Table(name = Constants.SYS_TABLE_PREFIX + "permission_r"
        , comment = "权限关联表"
        , module = Constants.SYS_MODULE_NAME
        , genInfo = false)
public class PermissionRelation {
    @Column(comment = "权限id", length = 50, name = "permission_id")
    private String permissionId;
    @Column(comment = "关联id", length = 100, name = "ref_id")
    private String refId;
    @Column(comment = "关联类型", length = 100, name = "ref_type")
    private String refType;

    public String getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(String permissionId) {
        this.permissionId = permissionId;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public String getRefType() {
        return refType;
    }

    public void setRefType(String refType) {
        this.refType = refType;
    }
}
