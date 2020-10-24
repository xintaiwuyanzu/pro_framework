package com.dr.framework.sys.service;

import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.Table;
import com.dr.framework.core.util.Constants;

/**
 * @author dr
 */
@Table(name = Constants.SYS_TABLE_PREFIX + "role_group"
        , comment = "角色人员组关联表"
        , module = Constants.SYS_MODULE_NAME)
class EntityRoleGroup extends EntityAbstractRoleRelation {
    @Column(length = 50, comment = "人员组id")
    private String groupId;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
