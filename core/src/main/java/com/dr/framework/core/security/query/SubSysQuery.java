package com.dr.framework.core.security.query;

import com.dr.framework.common.query.IdQuery;

/**
 * 子系统查询工具类
 *
 * @author dr
 */
public class SubSysQuery extends IdQuery {

    private SubSysQuery() {
    }

    public static class Builder extends IdQuery.Builder<SubSysQuery, Builder> {
        SubSysQuery query = new SubSysQuery();

        @Override
        public SubSysQuery getQuery() {
            return query;
        }
    }


}
