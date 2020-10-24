package com.dr.framework.core.security.entity;

import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.Table;
import com.dr.framework.core.util.Constants;

/**
 * 权限
 * 权限跟资源关联，
 * 权限编码采用三段式：资源Id或编码:资源操作:数据Id
 * <p>
 * 上述三段都可以使用*作为通配符，其中资源操作可以有多个，以逗号隔开
 * <p>
 * 权限编码可以有多个，以;隔开
 *
 * @author dr
 */
@Table(name = Constants.SYS_TABLE_PREFIX + "permission"
        , comment = "权限"
        , module = Constants.SYS_MODULE_NAME
        , genInfo = false)
public class Permission extends AbstractSecurityRelation {
    @Column(name = "security_code", length = 5000, comment = "权限编码")
    private String code;
    @Column(length = 100, comment = "分组Id")
    private String group;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
