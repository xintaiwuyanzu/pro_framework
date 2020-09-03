package com.dr.framework.core.orm.support.mybatis;

import com.dr.framework.core.orm.support.mybatis.spring.MybatisConfigurationBean;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;

/**
 * 能够解析特定方言的sql语句
 *
 * @author dr
 */
public class MultiDataSourceLangDriver extends XMLLanguageDriver {
    @Override
    public SqlSource createSqlSource(Configuration configuration, XNode script, Class<?> parameterType) {
        return super.createSqlSource(configuration, script, parameterType);
    }

    @Override
    public SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType) {
        if (configuration instanceof MybatisConfigurationBean) {
            script = ((MybatisConfigurationBean) configuration).getDataSourceProperties()
                    .getDialect()
                    .parseDialectSql(script);
        }
        return super.createSqlSource(configuration, script, parameterType);
    }
}
