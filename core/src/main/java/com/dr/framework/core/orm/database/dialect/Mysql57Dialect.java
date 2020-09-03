package com.dr.framework.core.orm.database.dialect;

import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;

/**
 * mysql数据库5.7及以上
 *
 * @author dr
 */
public class Mysql57Dialect extends MysqlDialect {
    public Mysql57Dialect() {
        super();
        registerClass(1, java.sql.Date.class).registerType(Types.DATE, "date");
        registerClass(1, Time.class).registerType(Types.TIME, "time(6)");
        registerClass(1, Timestamp.class).registerType(Types.TIMESTAMP, "datetime(6)");
        registerClass(1, java.util.Date.class).registerType(Types.TIMESTAMP, "datetime(6)");
    }
}
