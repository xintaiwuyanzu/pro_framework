package com.dr.framework.core.orm.support.mybatis.page;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.RowBounds;
import org.springframework.util.StringUtils;

class SQLServer2008Dialect implements Dialect {
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
    public String parseToPageSql(MappedStatement mappedStatement, String originalSql, RowBounds rowBounds) {
        StringBuilder pagingBuilder = new StringBuilder();
        String orderby = getOrderByPart(originalSql);
        String distinctStr = "";
        String loweredString = originalSql.toLowerCase();
        String sqlPartString = originalSql;
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
        long firstParam = rowBounds.getOffset() + 1;
        long secondParam = rowBounds.getOffset() + rowBounds.getLimit();
        String sql = "WITH selectTemp AS (SELECT " + distinctStr + "TOP 100 PERCENT " +
                " ROW_NUMBER() OVER (" + orderby + ") as __row_number__, " + pagingBuilder +
                ") SELECT * FROM selectTemp WHERE __row_number__ BETWEEN " +
                firstParam + " AND " + secondParam + " ORDER BY __row_number__";
        return sql;
    }

    @Override
    public String getName() {
        return "sqlserver";
    }
}
