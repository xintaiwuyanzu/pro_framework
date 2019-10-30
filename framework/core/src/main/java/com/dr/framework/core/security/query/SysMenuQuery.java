package com.dr.framework.core.security.query;

import com.dr.framework.common.query.IdQuery;

/**
 * 菜单查询工具类
 *
 * @author dr
 */
public class SysMenuQuery extends IdQuery {
    private String sysId;

    private SysMenuQuery() {
    }

    public static class Builder extends IdQuery.Builder<SysMenuQuery, Builder> {
        SysMenuQuery query = new SysMenuQuery();

        public Builder sysIdEqual(String sysId) {
            query.setSysId(sysId);
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

}
