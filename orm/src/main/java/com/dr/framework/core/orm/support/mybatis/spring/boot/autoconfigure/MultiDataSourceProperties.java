package com.dr.framework.core.orm.support.mybatis.spring.boot.autoconfigure;

import com.dr.framework.core.orm.database.DataBaseMetaData;
import com.dr.framework.core.orm.database.Dialect;
import com.dr.framework.core.orm.support.mybatis.spring.DriverUtils;
import com.zaxxer.hikari.HikariDataSource;
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

    private String prefix;
    private boolean useXa = true;
    private DataSource selfManagedDatasource;
    private DataBaseMetaData dataBaseMetaData;

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        selfManagedDatasource = DriverUtils.buildDataSource(this);
        if (selfManagedDatasource instanceof HikariDataSource) {
            //TODO oracle 默认不读取注释信息的，得在这里加 remarksReporting 属性。没想到比较好的办法
            ((HikariDataSource) selfManagedDatasource).getDataSourceProperties().setProperty("remarksReporting", "true");
            ((HikariDataSource) selfManagedDatasource).getDataSourceProperties().setProperty("remarksReporting", "true");
        }
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

    public boolean isUseXa() {
        return useXa;
    }

    public void setUseXa(boolean useXa) {
        this.useXa = useXa;
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

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
