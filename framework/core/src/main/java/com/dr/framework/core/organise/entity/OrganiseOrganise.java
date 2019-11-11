package com.dr.framework.core.organise.entity;

import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.Table;
import com.dr.framework.core.util.Constants;

/**
 * 机构树关联表
 *
 * @author dr
 */
@Table(name = Constants.SYS_TABLE_PREFIX + "organise_organise"
        , comment = "机构树关联表"
        , module = Constants.SYS_MODULE_NAME
        , genInfo = false)
public class OrganiseOrganise {
    @Column(name = "parent_id", comment = "父机构id", length = 100)
    private String parentId;
    @Column(name = "organise_id", comment = "机构id", length = 100)
    private String organiseId;
    @Column(name = "is_default", comment = "是否默认")
    private boolean isDefault;
    @Column(name = "group_id", comment = "所属分组", length = 50)
    private String groupId;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getOrganiseId() {
        return organiseId;
    }

    public void setOrganiseId(String organiseId) {
        this.organiseId = organiseId;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }
}
