package com.dr.framework.core.security.entity;

import com.dr.framework.common.entity.BaseStatusEntity;
import com.dr.framework.core.orm.annotations.Table;
import com.dr.framework.core.util.Constants;

/**
 * @author dr
 */
@Table(name = Constants.SYS_TABLE_PREFIX + "role"
        , comment = "角色"
        , module = Constants.SYS_MODULE_NAME
        , genInfo = false)
public class Role extends BaseStatusEntity<Integer> {
    private String code;
    private String description;
    private boolean isSys;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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
}
