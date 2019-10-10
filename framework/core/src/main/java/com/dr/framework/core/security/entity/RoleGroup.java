package com.dr.framework.core.security.entity;

import com.dr.framework.core.orm.annotations.Table;
import com.dr.framework.core.util.Constants;

/**
 * @author dr
 */
@Table(name = Constants.SYS_TABLE_PREFIX + "role_group"
        , comment = "角色组"
        , module = Constants.SYS_MODULE_NAME
        , genInfo = false)
public class RoleGroup {

}
