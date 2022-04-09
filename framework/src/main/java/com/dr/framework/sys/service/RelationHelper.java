package com.dr.framework.sys.service;

import com.dr.framework.core.organise.entity.Person;
import com.dr.framework.core.orm.module.EntityRelation;
import com.dr.framework.core.orm.sql.support.SqlQuery;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 用来辅助构建sqlquery类
 */
interface RelationHelper {
    default void checkBuildInQuery(EntityRelation relation, SqlQuery query, String columnName, List values) {
        if (values != null && !values.isEmpty()) {
            query.in(relation.getColumn(columnName), values);
        }
    }

    default void checkBuildNotInQuery(EntityRelation relation, SqlQuery<Person> query, String columnName, List values) {
        if (values != null && !values.isEmpty()) {
            query.notIn(relation.getColumn(columnName), values);
        }
    }

    default void checkBuildLikeQuery(EntityRelation relation, SqlQuery query, String columnName, String value) {
        if (StringUtils.hasText(value)) {
            query.like(relation.getColumn(columnName), value);
        }
    }

    default void checkBuildNotLikeQuery(EntityRelation relation, SqlQuery query, String columnName, String value) {
        if (StringUtils.hasText(value)) {
            query.notLike(relation.getColumn(columnName), value);
        }
    }
}
