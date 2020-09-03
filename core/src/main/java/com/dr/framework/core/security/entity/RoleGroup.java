package com.dr.framework.core.security.entity;

import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.Table;
import com.dr.framework.core.util.Constants;

/**
 * @author dr
 */
@Table(name = Constants.SYS_TABLE_PREFIX + "role_group"
        , comment = "角色人员组关联表"
        , module = Constants.SYS_MODULE_NAME
        , genInfo = false)
public class RoleGroup extends BaseRoleRelation {
    @Column(length = 50, comment = "人员组id")
    private String groupId;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
