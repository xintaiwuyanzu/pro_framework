package com.dr.framework.core.orm.support.mybatis.spring.boot.autoconfigure;

import com.dr.framework.core.orm.database.DataBaseMetaData;
import com.dr.framework.core.orm.database.Dialect;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

import javax.sql.DataSource;
import java.io.Closeable;
import java.util.Collections;
import java.util.List;

/**
 * 配置多数据源使用，多了几个属性
 *
 * @author dr
 */
public class MultiDataSourceProperties extends DataSourceProperties {
    public static final String DDL_VALIDATE = "validate";
    public static final String DDL_NONE = "none";
    public static final String DDL_UPDATE = "update";

    /**
     * 建表策略
     */
    private String autoDDl = DDL_VALIDATE;
    /**
     * 包含的模块
     */
    private List<String> includeModules = Collections.emptyList();
    /**
     * 排除的模块
     */
    private List<String> excludeModules = Collections.emptyList();
    /**
     * 是否包含被xa事物管理
     */
    private boolean xa = true;
    private DataSource selfManagedDatasource;
    private DataBaseMetaData dataBaseMetaData;


    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        selfManagedDatasource = initializeDataSourceBuilder().build();
        dataBaseMetaData = new DataBaseMetaData(selfManagedDatasource, getName());
    }

    /**
     * 是否包含指定模块
     *
     * @param module
     * @return
     */
    public boolean containModules(String module) {
        for (String m : excludeModules) {
            if (m.equalsIgnoreCase(module)) {
                return false;
            }
        }
        for (String m : includeModules) {
            if (m.equalsIgnoreCase(module)) {
                return true;
            }
        }
        return false;
    }

    public String getAutoDDl() {
        return autoDDl;
    }

    public void setAutoDDl(String autoDDl) {
        this.autoDDl = autoDDl;
    }

    public List<String> getIncludeModules() {
        return includeModules;
    }

    public void setIncludeModules(List<String> includeModules) {
        this.includeModules = includeModules;
    }

    public List<String> getExcludeModules() {
        return excludeModules;
    }

    public void setExcludeModules(List<String> excludeModules) {
        this.excludeModules = excludeModules;
    }

    public boolean isXa() {
        return xa;
    }

    public void setXa(boolean xa) {
        this.xa = xa;
    }

    public void close() throws Exception {
        if (selfManagedDatasource instanceof DisposableBean) {
            ((DisposableBean) selfManagedDatasource).destroy();
        }
        if (selfManagedDatasource instanceof Closeable) {
            ((Closeable) selfManagedDatasource).close();
        }
    }

    public Dialect getDialect() {
        return getDataBaseMetaData().getDialect();
    }

    public DataBaseMetaData getDataBaseMetaData() {
        return dataBaseMetaData;
    }
}
