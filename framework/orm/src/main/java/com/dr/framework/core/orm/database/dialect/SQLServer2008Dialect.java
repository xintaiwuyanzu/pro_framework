package com.dr.framework.core.orm.database.dialect;

import com.dr.framework.core.orm.database.Dialect;
import org.springframework.util.StringUtils;

public  class SQLServer2008Dialect extends Dialect {
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
        if (StringUtils.isEmpty(orderby)) {
            orderby = "ORDER BY CURRENT_TIMESTAMP";
        }
        long firstParam = offset + 1;
        long secondParam = offset + limit;
        String sql = "WITH selectTemp AS (SELECT " + distinctStr + "TOP 100 PERCENT " +
                " ROW_NUMBER() OVER (" + orderby + ") as __row_number__, " + pagingBuilder +
                ") SELECT * FROM selectTemp WHERE __row_number__ BETWEEN " +
                firstParam + " AND " + secondParam + " ORDER BY __row_number__";
        return sql;
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
        return "sqlserver";
    }
}
