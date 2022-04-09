package com.dr.framework.core.orm.database.dialect;

import com.dr.framework.core.orm.database.Dialect;
import com.dr.framework.core.orm.jdbc.Column;
import com.dr.framework.core.orm.jdbc.Relation;
import org.springframework.util.StringUtils;

import java.sql.*;

/**
 * @author dr
 */
public class MysqlDialect extends Dialect {
    public static final String NAME = "mysql";

    public MysqlDialect() {
        //字符串
        registerClass(String.class)
                .registerType(Types.CHAR, 1, "char(1)")
                .registerType(Types.NCHAR, 1, "char(1)")
                .registerType(Types.VARCHAR, 255, "varchar($l)")
                .registerType(Types.NVARCHAR, 255, "varchar($l)")
                .registerType(Types.LONGVARCHAR, 65535, "text")
                .registerType(Types.LONGNVARCHAR, 65535, "text")

                .registerType(Types.LONGVARCHAR, 16777215, "mediumtext")
                .registerType(Types.LONGNVARCHAR, 16777215, "mediumtext")

                .registerType(Types.LONGVARCHAR, 4294967295L, "longtext")
                .registerType(Types.LONGNVARCHAR, 4294967295L, "longtext")

                .registerType(Types.SQLXML, 4294967295L, "longtext");
        //数字
        registerClass(int.class, Integer.class
                , double.class, Double.class
                , float.class, Float.class
                , long.class, Long.class)
                //2的8次方  -127到127
                .registerType(Types.TINYINT, 2, "tinyint")
                .registerType(Types.SMALLINT, 4, "smallint")
                .registerType(Types.INTEGER, 8, "mediumint")
                .registerType(Types.INTEGER, 10, "integer")
                .registerType(Types.BIGINT, 19, "bigint")
                /**
                 * DECIMAL[(M[,D])]
                 * The maximum number of digits (M) for DECIMAL is 65.
                 * The maximum number of supported decimals (D) is 30.
                 * If D is omitted, the default is 0.
                 * If M is omitted, the default is 10.
                 */
                .registerType(Types.DECIMAL, 65, "decimal($p,$s)")
                .registerType(Types.NUMERIC, 65, "decimal($p,$s)")
                .registerType(Types.REAL, 65, "decimal($p,$s)")
                .registerType(Types.DOUBLE, 65, "decimal($p,$s)")
                .registerType(Types.FLOAT, 65, "decimal($p,$s)");
        //日期
        registerClass(Date.class).registerType(Types.DATE, "date");
        registerClass(Time.class).registerType(Types.TIME, "time");
        registerClass(Timestamp.class).registerType(Types.TIMESTAMP, "datetime");
        registerClass(java.util.Date.class).registerType(Types.TIMESTAMP, "datetime");
        //blob
        registerClass(Blob.class, Byte[].class, byte[].class)
                .registerType(Types.BLOB, "longblob")
                .registerType(Types.BINARY, "binary($l)")
                .registerType(Types.VARBINARY, 255, "tinyblob")
                .registerType(Types.VARBINARY, 65535, "blob")
                .registerType(Types.VARBINARY, 16777215, "mediumblob")
                .registerType(Types.LONGVARBINARY, "longblob")
                .registerType(Types.LONGVARBINARY, 16777215, "mediumblob");
        //clob
        registerClass(Clob.class).registerType(Types.CLOB, "longtext");
        registerClass(NClob.class).registerType(Types.NCLOB, "longtext");
        //boolean
        registerClass(boolean.class, Boolean.class).registerType(Types.BOOLEAN, "bit");
        registerClass(boolean.class, Boolean.class).registerType(Types.BIT, "bit");
    }

    @Override
    public String parseToPageSql(String sqlSource, int offset, int limit) {
        StringBuilder builder = new StringBuilder(sqlSource);
        builder.append(" limit ").append(offset).append(",").append(limit);
        return builder.toString();
    }

    @Override
    protected String getName() {
        return NAME;
    }

    //TODO mysql8
    @Override
    protected String getTableRemark(String remark) {
        StringBuilder sb = new StringBuilder(" default charset=utf8 ");
        if (StringUtils.hasText(remark)) {
            sb.append(" comment='")
                    .append(remark)
                    .append("' ");
        }
        return sb.toString();
    }

    @Override
    protected String getColumnRemark(String remark) {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.hasText(remark)) {
            sb.append(" comment '")
                    .append(remark)
                    .append("' ");
        }
        return sb.toString();
    }

    /**
     * alter table tableName change oldName newName type(100) null comment '';
     *
     * @param newColumn
     * @param oldColumn
     * @param renameColumnName
     * @return
     */
    @Override
    protected String getRenameColumnSql(Column newColumn, Column oldColumn, String renameColumnName) {
        StringBuffer sb = new StringBuffer(String.format(" %s  change  %s %s"
                , getAlterTableString(oldColumn.getTableName())
                , convertObjectName(oldColumn.getName())
                , convertObjectName(renameColumnName)))
                .append(' ');
        sb.append(getColumnType(oldColumn));
        appendColumnBaseInfo(sb, oldColumn);
        //baseInfo 中带有备注
        /*if (StringUtils.hasText(newColumn.getRemark())) {
            sb.append(" comment ")
                    .append(newColumn.getRemark());
        }*/
        return sb.toString();
    }

    @Override
    protected String getModifyColumnString() {
        return "modify";
    }

    @Override
    protected String getDropIndexSql(Relation relation, String indexName) {
        return String.format("drop index %s on %s", convertObjectName(indexName), convertObjectName(relation.getName()));
    }
}
