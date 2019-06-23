package com.dr.framework.core.orm.database.tools;

/**
 * 封装表创建信息
 *
 * @author dr
 */
public class DataBaseChangeInfo {
    /**
     * 数据库ddl sql 语句
     */
    private String sql;
    /**
     * 数据库更新消息提醒，用来打印或者查看使用
     */
    private String message;

    public DataBaseChangeInfo(String sql, String message) {
        this.sql = sql;
        this.message = message;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
