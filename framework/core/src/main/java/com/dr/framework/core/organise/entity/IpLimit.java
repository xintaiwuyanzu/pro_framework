package com.dr.framework.core.organise.entity;

import com.dr.framework.common.entity.BaseStatusEntity;
import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.Table;
import com.dr.framework.core.util.Constants;

/**
 * 绑定登录ip
 *
 * @author dr
 */
@Table(name = Constants.SYS_TABLE_PREFIX + "ip_limit"
        , comment = "绑定登录ip"
        , module = Constants.SYS_MODULE_NAME
        , genInfo = false)
public class IpLimit extends BaseStatusEntity<String> {
    @Column(name = "person_id", length = 50)
    private String personId;
    @Column(name = "ip_define", length = 50)
    private String ip;
    @Column(comment = "包含还是排除")
    private boolean exclude;

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public boolean isExclude() {
        return exclude;
    }

    public void setExclude(boolean exclude) {
        this.exclude = exclude;
    }
}
