package com.dr.framework.core.orm.database.dialect;

/**
 * @author dr
 */
public class SQLServer2012Dialect extends SQLServer2008Dialect {
    public SQLServer2012Dialect() {
        super();
    }

    @Override
    public String parseToPageSql(String sqlSource, int offset, int limit) {
        //如果有order by 语句，则使用offset
        if (hasOrderBy(sqlSource)) {
            StringBuilder builder = new StringBuilder(sqlSource);
            builder.append(" OFFSET ").append(offset).append(" ROWS FETCH NEXT ");
            builder.append(limit).append(" ROWS ONLY");
            return builder.toString();
        }
        //如果没有，则使用默认的
        return super.parseToPageSql(sqlSource, offset, limit);
    }

    private boolean hasOrderBy(String sql) {
        int depth = 0;
        String lowerCaseSQL = sql.toLowerCase();
        for (int i = lowerCaseSQL.length() - 1; i >= 0; --i) {
            char ch = lowerCaseSQL.charAt(i);
            if (ch == '(') {
                depth++;
            } else if (ch == ')') {
                depth--;
            }
            if (depth == 0) {
                if (lowerCaseSQL.startsWith("order by ", i)) {
                    return true;
                }
            }
        }
        return false;
    }
}
