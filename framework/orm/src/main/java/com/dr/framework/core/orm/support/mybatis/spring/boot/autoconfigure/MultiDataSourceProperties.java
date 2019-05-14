package com.dr.framework.core.orm.support.mybatis.spring.boot.autoconfigure;

import com.dr.framework.core.orm.database.DataBase;
import com.dr.framework.core.orm.database.DataBaseMetaData;
import com.dr.framework.core.orm.database.Dialect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

import javax.sql.DataSource;
import java.io.Closeable;
import java.sql.Connection;
import java.sql.SQLException;
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
    Logger logger = LoggerFactory.getLogger(MultiDataSourceProperties.class);
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
    private Dialect dialect;

    public Dialect getDialect() {
        if (dialect == null) {
            synchronized (MultiDataSourceProperties.class) {
                if (dialect == null) {
                    DataBaseMetaData dataBaseMetaData = getDataBaseMetaData();
                    for (DataBase dataBase : DataBase.values()) {
                        if (dataBase.match(dataBaseMetaData.getDatabaseProductName())) {
                            Class<? extends Dialect> dialectClass = dataBase.getDialectClass(dataBaseMetaData);
                            try {
                                dialect = dialectClass.newInstance();
                            } catch (Exception e) {
                                logger.error("初始化数据库方言失败", e);
                            }
                        }
                    }
                }
            }
        }
        return dialect;
    }

    public DataBaseMetaData getDataBaseMetaData() {
        if (dataBaseMetaData == null) {
            synchronized (MultiDataSourceProperties.class) {
                if (dataBaseMetaData == null) {
                    Connection connection = null;
                    try {
                        connection = getselfManagedConnection();
                        dataBaseMetaData = DataBaseMetaData.from(connection.getMetaData());
                    } catch (Exception e) {
                        logger.error("读取数据库基本信息失败", e);
                    } finally {
                        if (connection != null) {
                            try {
                                connection.close();
                            } catch (SQLException e) {
                                logger.error("关闭数据库链接失败", e);
                            }
                        }
                    }
                }
            }
        }
        return dataBaseMetaData;
    }

    public Connection getselfManagedConnection() throws Exception {
        return getseleManagedDataSource().getConnection();
    }

    private synchronized DataSource getseleManagedDataSource() {
        if (selfManagedDatasource == null) {
            selfManagedDatasource = initializeDataSourceBuilder().build();
        }
        return selfManagedDatasource;
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
}