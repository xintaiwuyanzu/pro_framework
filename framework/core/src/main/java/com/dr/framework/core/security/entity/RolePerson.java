package com.dr.framework.core.security.entity;

import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.Table;
import com.dr.framework.core.util.Constants;

/**
 * @author dr
 */
@Table(name = Constants.SYS_TABLE_PREFIX + "role_person"
        , comment = "人员角色关联表"
        , module = Constants.SYS_MODULE_NAME
        , genInfo = false)
public class RolePerson extends BaseRoleRelation {
    @Column(length = 50, comment = "人员id")
    private String personId;

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }
}
