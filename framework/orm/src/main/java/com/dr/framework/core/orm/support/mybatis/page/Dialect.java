package com.dr.framework.core.orm.support.mybatis.page;

import com.dr.framework.core.orm.support.mybatis.spring.MybatisConfigurationBean;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 方言模块，用于处理不同数据库的分页查询语句
 */
public interface Dialect {
    Logger logger = LoggerFactory.getLogger(Dialect.class);
    Map<Configuration, Dialect> configDialectMap = new ConcurrentHashMap<>();

    static String pageSql(MappedStatement mappedStatement, String sql, RowBounds rowBounds) {
        Dialect dialect = getDialect(mappedStatement);
        if (dialect != null) {
            return dialect.parseToPageSql(mappedStatement, sql, rowBounds);
        }
        return sql;
    }

    static String parseSql(MybatisConfigurationBean configuration, String sql) {
        return getDialect(configuration).parseDialectSql(sql);
    }


    static Dialect getDialect(MappedStatement mappedStatement) {
        return configDialectMap.get(mappedStatement.getConfiguration());
    }

    static Dialect getDialect(Configuration configuration) {
        if (!configDialectMap.containsKey(configuration)) {
            DataSource dataSource = configuration.getEnvironment().getDataSource();
            Dialect dialect = createDialect(dataSource);
            if (dialect != null) {
                configDialectMap.put(configuration, dialect);
            }
        }
        return configDialectMap.get(configuration);
    }

    /**
     * todo  金仓数据库还有其他的国产数据库
     *
     * @param dataSource 数据源
     * @return 返回指定数据源的方言类
     */
    static Dialect createDialect(DataSource dataSource) {
        Dialect dialect = null;
        Connection connection = null;
        String product = null;
        try {
            connection = dataSource.getConnection();
            DatabaseMetaData metaData = connection.getMetaData();
            product = metaData.getDatabaseProductName();
            int version = metaData.getDatabaseMajorVersion();
            switch (product) {
                case "MySQL":
                case "HSQL Database Engine":
                    //可能是MariaDb数据库
                    dialect = new MysqlDialect();
                    break;
                case "H2":
                case "SQLite":
                    dialect = new H2Dialect();
                    break;
                case "PostgreSQL":
                    //可能是瀚高数据库
                    dialect = new PostgreDialect();
                    break;
                case "Microsoft SQL Server":
                    dialect = new SQLServerDialect();
                    break;
                case "Oracle":
                    dialect = new OracleDialect();
                    break;
                default:
                    break;
            }

        } catch (Exception e) {
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                }
            }
        }
        Assert.notNull(dialect, "没找到【" + product + "】的分页处理类，请联系管理员添加！");
        return dialect;
    }

    /**
     * 将sql语句转换为物理分页
     *
     * @param mappedStatement mappedStatement
     * @param sql             SQL语句
     * @param rowBounds       分页参数
     * @return 返回解析后的sql语句
     */
    String parseToPageSql(MappedStatement mappedStatement, String sql, RowBounds rowBounds);

    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

    /**
     * 解析sql注解中的sql语句为适用特定数据库的sql
     *
     * @param sql
     * @return
     */
    default String parseDialectSql(String sql) {
        if (StringUtils.isEmpty(sql)) {
            return sql;
        } else if (sql.startsWith("<") && !sql.startsWith("<script>") && !sql.startsWith("<SCRIPT>")) {
            try {
                DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
                Document document = documentBuilder.parse(new ByteArrayInputStream(String.format("<scripts>%s</scripts>", sql).getBytes()));
                Node defaultNode = null;
                Node dialectNode = null;
                NodeList nodeList = document.getChildNodes().item(0).getChildNodes();
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node node = nodeList.item(i);
                    String nodeName = node.getNodeName();
                    if (nodeName.equalsIgnoreCase("default")) {
                        defaultNode = node;
                    } else if (nodeName.equalsIgnoreCase(getName())) {
                        dialectNode = node;
                    }
                }
                if (dialectNode != null) {
                    return dialectNode.getTextContent();
                }
                if (defaultNode != null) {
                    return defaultNode.getTextContent();
                }
            } catch (Exception e) {
            }
        }
        return sql;
    }

    default String getName() {
        return getClass().getSimpleName().toLowerCase().replace("dialect", "").trim();
    }
}
