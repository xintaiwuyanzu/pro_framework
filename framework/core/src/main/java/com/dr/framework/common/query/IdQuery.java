package com.dr.framework.common.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IdQuery {
    /**
     * 主键
     */
    private List<String> ids;

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
}
