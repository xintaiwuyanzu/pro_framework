package com.dr.framework.core.orm.database.dialect;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;

/**
 * @author dr
 */
public class SQLServer2008Dialect extends SQLServer2005Dialect {
    public SQLServer2008Dialect() {
        super();
        registerClass(1, Date.class).registerType(Types.DATE, "date");
        registerClass(1, Time.class).registerType(Types.TIME, "time");
        registerClass(1, Timestamp.class).registerType(Types.TIMESTAMP, "datetime2");
        registerClass(1, java.util.Date.class).registerType(Types.TIMESTAMP, "datetime2");
    }

}
