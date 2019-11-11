package com.dr.framework.core.organise.entity;

import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.Id;
import com.dr.framework.core.orm.annotations.Table;
import com.dr.framework.core.util.Constants;

/**
 * 人员 人员组关联表
 *
 * @author dr
 */
@Table(name = Constants.SYS_TABLE_PREFIX + "person_group_r"
        , comment = "人员组关联表"
        , module = Constants.SYS_MODULE_NAME
        , genInfo = false)
public class PersonGroupRelation {
    @Id
    @Column(length = 50)
    private String personId;
    @Id
    @Column(length = 50)
    private String groupId;

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
