package com.dr.framework.core.security.query;

import com.dr.framework.common.query.IdQuery;

/**
 * @author dr
 */
public class RoleQuery extends IdQuery {
    private String codeLike;

    private RoleQuery() {
    }

    public static class Builder extends IdQuery.Builder<RoleQuery, Builder> {
        RoleQuery query = new RoleQuery();

        public Builder codeLike(String code) {
            query.codeLike = code;
            return this;
        }

        @Override
        public RoleQuery getQuery() {
            return query;
        }
    }

    public String getCodeLike() {
        return codeLike;
    }

    public void setCodeLike(String codeLike) {
        this.codeLike = codeLike;
    }

}
