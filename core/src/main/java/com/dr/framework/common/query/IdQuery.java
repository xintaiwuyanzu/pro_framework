package com.dr.framework.common.query;


import com.dr.framework.core.orm.sql.Column;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 根据主键查询数据父类
 *
 * @author dr
 */
public class IdQuery {
    /**
     * 主键
     */
    private List<String> ids;

    private List<Column> orderBy;
    private List<Column> orderByDesc;

    public static abstract class Builder<T extends IdQuery, B extends Builder> {
        public abstract T getQuery();

        /**
         * 主键等于
         *
         * @param ids
         * @return
         */
        public B idEqual(String... ids) {
            if (ids.length > 0) {
                if (getQuery().getIds() == null) {
                    getQuery().setIds(new ArrayList<>());
                }
                getQuery().getIds().addAll(Arrays.asList(ids));
            }
            return (B) this;
        }

        public B orderBy(Column... columns) {
            if (getQuery().getOrderBy() == null) {
                getQuery().setOrderBy(new ArrayList<>());
            }
            getQuery().setOrderBy(Arrays.asList(columns));
            return (B) this;
        }

        public B orderByDesc(Column... columns) {
            if (getQuery().getOrderByDesc() == null) {
                getQuery().setOrderByDesc(new ArrayList<>());
            }
            getQuery().setOrderByDesc(Arrays.asList(columns));
            return (B) this;
        }

        public T build() {
            return getQuery();
        }

    }

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    public List<Column> getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(List<Column> orderBy) {
        this.orderBy = orderBy;
    }

    public List<Column> getOrderByDesc() {
        return orderByDesc;
    }

    public void setOrderByDesc(List<Column> orderByDesc) {
        this.orderByDesc = orderByDesc;
    }
}
