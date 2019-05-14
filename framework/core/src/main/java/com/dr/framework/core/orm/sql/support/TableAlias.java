package com.dr.framework.core.orm.sql.support;

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
        if (StringUtils.isEmpty(alia)) {
            return buildAlia(table);
        }
        return alia;
    }

    private String buildAlia(String table) {
        if (!alias.containsKey(table)) {
            return null;
        }
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
