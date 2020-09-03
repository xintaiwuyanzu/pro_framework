package com.dr.framework.core.orm.database.dialect;

import com.dr.framework.core.orm.database.Dialect;
import com.dr.framework.core.orm.jdbc.Relation;
import org.springframework.util.StringUtils;

import java.util.Locale;

/**
 * oracle 父类
 *
 * @author dr
 */
public abstract class OracleDialect extends Dialect {
    public static final String NAME = "oracle";

    @Override
    public boolean supportCommentOn() {
        return true;
    }

    @Override
    public String convertObjectName(String source) {
        return StringUtils.hasLength(source) ? source.toUpperCase(Locale.ROOT).trim() : source;
    }

    @Override
    protected String getAddColumnString() {
        return "add";
    }

    @Override
    protected String getName() {
        return NAME;
    }

    @Override
    protected String getModifyColumnString() {
        return "modify";
    }

    @Override
    protected String getDropPrimaryKeySql(Relation jdbcTable) {
        return String.format("%s drop primary key drop index", getAlterTableString(jdbcTable.getName()));
    }
}
