package com.dr.framework.common.query;

import java.util.List;
import java.util.Map;

public class GenSourceQuery {
    private String datasource;
    private String path;
    private String packageName;
    private String module;
    private List<Map<String, Object>> tables;

    public String getDatasource() {
        return datasource;
    }

    public void setDatasource(String datasource) {
        this.datasource = datasource;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public List<Map<String, Object>> getTables() {
        return tables;
    }

    public void setTables(List<Map<String, Object>> tables) {
        this.tables = tables;
    }
}
