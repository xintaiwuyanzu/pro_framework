package com.dr.framework.core.orm.database;

import com.dr.framework.core.orm.jdbc.Relation;
import org.springframework.context.ApplicationEvent;

import javax.sql.DataSource;

/**
 * @Author: caor
 * @Date: 2023-07-24 23:37
 * @Description:
 */
public class TableRelationLoadEvent extends ApplicationEvent {

    private Dialect dialect;
    private DataSource dataSource;

    public TableRelationLoadEvent(Object source, Dialect dialect, DataSource dataSource) {
        super(source);
        this.dialect = dialect;
        this.dataSource = dataSource;
    }

    public Relation getRelation() {
        return (Relation) super.getSource();
    }

    public Dialect getDialect() {
        return dialect;
    }

    public DataSource getDataSource() {
        return dataSource;
    }
}
