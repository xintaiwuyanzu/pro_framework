package com.dr.framework.core.security.query;

import com.dr.framework.common.query.IdQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 菜单查询工具类
 *
 * @author dr
 */
public class SysMenuQuery extends IdQuery {
    private String sysId;
    private List<String> roleIdIn;
    private String personId;

    private SysMenuQuery() {
    }


    public static class Builder extends IdQuery.Builder<SysMenuQuery, Builder> {
        SysMenuQuery query = new SysMenuQuery();

        public Builder sysIdEqual(String sysId) {
            query.setSysId(sysId);
            return this;
        }

        public Builder personIdEqual(String personId) {
            query.setPersonId(personId);
            return this;
        }

        public Builder roleIdIn(String... roleIds) {
            if (roleIds.length > 0) {
                if (query.roleIdIn == null) {
                    query.roleIdIn = new ArrayList<>();
                }
                Collections.addAll(query.roleIdIn, roleIds);
            }
            return this;
        }

        @Override
        public SysMenuQuery getQuery() {
            return query;
        }
    }

    public String getSysId() {
        return sysId;
    }

    public void setSysId(String sysId) {
        this.sysId = sysId;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public List<String> getRoleIdIn() {
        return roleIdIn;
    }

    public void setRoleIdIn(List<String> roleIdIn) {
        this.roleIdIn = roleIdIn;
    }
}
