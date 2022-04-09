package com.dr.framework.core.orm.sql.support;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

class TableAlias {
    Map<String, String> alias = new HashMap<>();
    Map<String, String> autoGenAlias = new HashMap<>();
    static final String ARRANGE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    int randomIndex = 0;

    String alias(String table) {
        table = table.toUpperCase();
        String alia = alias.get(table);
        if (!StringUtils.hasText(alia)) {
            return buildAlia(table);
        }
        return alia;
    }

    private String buildAlia(String table) {
        Assert.isTrue(alias.containsKey(table), "sql语句中没有表【" + table + "】，请检查查询条件是否正确！");
        if (autoGenAlias.containsKey(table)) {
            return autoGenAlias.get(table);
        }
        String alia = genAlia();
        autoGenAlias.put(table, alia);
        return alia;
    }

    private String genAlia() {
        String alia = ARRANGE.substring(randomIndex, randomIndex + 1);
        while (alias.containsValue(alia) || autoGenAlias.containsValue(alia)) {
            randomIndex++;
            alia = ARRANGE.substring(randomIndex, randomIndex + 1);
        }
        return alia;
    }
}
