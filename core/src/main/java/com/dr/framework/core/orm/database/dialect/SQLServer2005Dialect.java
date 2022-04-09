package com.dr.framework.core.orm.database.dialect;

import com.dr.framework.core.orm.database.Dialect;
import com.dr.framework.core.orm.jdbc.Column;
import com.dr.framework.core.orm.jdbc.Relation;
import org.springframework.util.StringUtils;

import java.sql.*;

/**
 * @author dr
 */
public class SQLServer2005Dialect extends Dialect {

    public static final String NAME = "sqlserver";

    public SQLServer2005Dialect() {
        //字符串
        registerClass(String.class)
                .registerType(Types.CHAR, 1, "char(1)")
                .registerType(Types.NCHAR, 1, "nchar(1)")
                .registerType(Types.VARCHAR, 8000, "varchar($l)")
                .registerType(Types.NVARCHAR, 255, "nvarchar($l)")
                .registerType(Types.LONGVARCHAR, 2147483647, "varchar(max)")
                .registerType(Types.LONGNVARCHAR, 2147483647, "nvarchar(max)")
                .registerType(Types.SQLXML, 2147483647, "varchar(max)");
        //数字
        registerClass(int.class, Integer.class
                , double.class, Double.class
                , float.class, Float.class
                , long.class, Long.class)
                //2的8次方  -127到127
                .registerType(Types.TINYINT, 2, "tinyint")
                .registerType(Types.SMALLINT, 4, "smallint")
                .registerType(Types.INTEGER, 10, "int")
                .registerType(Types.BIGINT, 19, "bigint")
                /**
                 * decimal[ **(**p[ **,**s] )] 和 numeric[ **(**p[ **,**s] )]
                 *固定精度和小数位数。
                 *使用最大精度时，有效值从 - 10^38 +1 到 10^38 - 1。
                 * decimal 的 SQL-92 同义词为 dec 和 dec(p, s)。numeric 在功能上等价于 decimal。
                 */
                .registerType(Types.DECIMAL, 38, "decimal($p,$s)")
                .registerType(Types.NUMERIC, 38, "decimal($p,$s)")
                .registerType(Types.DOUBLE, 38, "decimal($p,$s)")
                .registerType(Types.REAL, 38, "decimal($p,$s)")
                .registerType(Types.FLOAT, 38, "decimal($p,$s)");
        //日期

        registerClass(Date.class).registerType(Types.DATE, "datetime");
        registerClass(Time.class).registerType(Types.TIME, "datetime");
        registerClass(Timestamp.class).registerType(Types.TIMESTAMP, "datetime");
        registerClass(java.util.Date.class).registerType(Types.TIMESTAMP, "datetime");
        //blob
        registerClass(Blob.class, Byte[].class, byte[].class)
                .registerType(Types.BLOB, "varbinary(MAX)")
                .registerType(Types.BINARY, 8000, "varbinary($l)")
                .registerType(Types.VARBINARY, 2147483647, "varbinary(MAX)")
                .registerType(Types.LONGVARBINARY, 2147483647, "varbinary(MAX)");
        //clob
        registerClass(Clob.class).registerType(Types.CLOB, "varchar(max)");
        registerClass(NClob.class).registerType(Types.NCLOB, "nvarchar(max)");
        //boolean
        registerClass(boolean.class, Boolean.class).registerType(Types.BOOLEAN, "bit");
        registerClass(boolean.class, Boolean.class).registerType(Types.BIT, "bit");
    }

    @Override
    public String parseToPageSql(String sqlSource, int offset, int limit) {
        StringBuilder pagingBuilder = new StringBuilder();
        String orderby = getOrderByPart(sqlSource);
        String distinctStr = "";
        String loweredString = sqlSource.toLowerCase();
        String sqlPartString = sqlSource;
        if (loweredString.trim().startsWith("select")) {
            int index = 6;
            if (loweredString.startsWith("select distinct")) {
                distinctStr = "DISTINCT ";
                index = 15;
            }
            sqlPartString = sqlPartString.substring(index);
        }
        pagingBuilder.append(sqlPartString);
        if (!StringUtils.hasText(orderby)) {
            orderby = "ORDER BY CURRENT_TIMESTAMP";
        }
        long firstParam = offset + 1;
        long secondParam = offset + limit;
        return "WITH selectTemp AS (SELECT " + distinctStr + "TOP 100 PERCENT " +
                " ROW_NUMBER() OVER (" + orderby + ") as __row_number__, " + pagingBuilder +
                ") SELECT * FROM selectTemp WHERE __row_number__ BETWEEN " +
                firstParam + " AND " + secondParam + " ORDER BY __row_number__";
    }

    private String getOrderByPart(String sql) {
        String loweredString = sql.toLowerCase();
        int orderByIndex = loweredString.indexOf("order by");
        if (orderByIndex != -1) {
            return sql.substring(orderByIndex);
        } else {
            return "";
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean canIndex(Column column) {
        int columnType = column.getType();
        if (columnType == Types.CHAR
                || columnType == Types.NCHAR
                || columnType == Types.VARCHAR
                || columnType == Types.NVARCHAR
        ) {
            //字符串长于900，不能建立索引
            return column.getSize() <= 900;
        }
        return super.canIndex(column);
    }

    @Override
    protected String getAddColumnString() {
        return "add";
    }

    @Override
    protected String getRenameColumnSql(Column newColumn,Column oldColumn, String renameColumnName) {
        return String.format("exec sp_rename '%s.%s','%s'"
                , convertObjectName(newColumn.getTableName())
                , convertObjectName(newColumn.getName())
                , convertObjectName(renameColumnName));
    }

    @Override
    protected String getDropPrimaryKeySql(Relation jdbcTable) {
        return String.format("%s drop constraint %s", getAlterTableString(jdbcTable.getName()), jdbcTable.getPrimaryKeyName());
    }

    @Override
    protected String getDropIndexSql(Relation relation, String indexName) {
        return String.format("drop index %s on %s", convertObjectName(indexName), convertObjectName(relation.getName()));
    }
}
