package com.dr.framework.core.orm.support.mybatis.spring;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

public class DriverUtils {

    public static DataSource buildDataSource(DataSourceProperties properties) {
        if (!StringUtils.isEmpty(properties.getUrl()) && StringUtils.isEmpty(properties.getDriverClassName())) {
            if (isDm(properties.getUrl())) {
                properties.setDriverClassName("dm.jdbc.driver.DmDriver");
            }
        }
        return properties.initializeDataSourceBuilder().build();
    }


    public static String xaDriverClass(String url) {
        Assert.isTrue(!StringUtils.isEmpty(url), "数据库链接不能为空！");
        DatabaseDriver driver = DatabaseDriver.fromJdbcUrl(url);
        if (driver != null) {
            return driver.getXaDataSourceClassName();
        }
        //达梦数据库
        if (isDm(url)) {
            return "dm.jdbc.driver.DmdbXADataSource";
        }
        throw new IllegalArgumentException("未找到：" + url + ",对应的xa数据库驱动！");
    }

    /**
     * 是否达梦数据库
     *
     * @param url
     * @return
     */
    static boolean isDm(String url) {
        return url.trim().startsWith("jdbc:dm");
    }
}
