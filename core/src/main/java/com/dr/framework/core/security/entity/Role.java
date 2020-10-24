package com.dr.framework.core.security.entity;

import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.Table;
import com.dr.framework.core.util.Constants;

/**
 * @author dr
 */
@Table(name = Constants.SYS_TABLE_PREFIX + "role"
        , comment = "角色"
        , module = Constants.SYS_MODULE_NAME
        , genInfo = false)
public class Role extends AbstractSecurityRelation {

    public static final String USER_DEFAULT_ROLE = "userDefault";
    @Column(name = "security_code", length = 500, comment = "角色编码")
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
