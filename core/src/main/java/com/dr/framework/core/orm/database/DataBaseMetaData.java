package com.dr.framework.core.orm.database;

import com.dr.framework.core.orm.database.dialect.Oracle8iDialect;
import com.dr.framework.core.orm.jdbc.Column;
import com.dr.framework.core.orm.jdbc.Relation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据库链接信息,缓存数据库链接信息
 *
 * @author dr
 * @see java.sql.DatabaseMetaData
 */
public class DataBaseMetaData {

    private static final String SQL_FILTER_MATCH_ALL = "%";


    private int versionOfNoVersion(int version) {
        return version < 0 ? NO_VERSION : version;
    }

    final int NO_VERSION = Integer.MIN_VALUE;

    private Logger logger = LoggerFactory.getLogger(DataBaseMetaData.class);
    /**
     * 数据库产品名称
     */
    private String databaseProductName;
    /**
     * 数据库主版本号
     */
    private int databaseMajorVersion;
    /**
     * 数据库次版本号
     */
    private int databaseMinorVersion;
    /**
     * 数据库链接驱动名称
     */
    private String driverName;
    /**
     * 数据库驱动主版本号
     */
    private int driverMajorVersion;
    /**
     * 数据库次版本号
     */
    private int driverMinorVersion;
    private String catalog;
    private String schema;
    private String name;
    private Map<String, Relation<Column>> tableMap = Collections.synchronizedMap(new HashMap<>());
    private Dialect dialect;
    private DataSource dataSource;


    private String host, userName, password;
    private Integer port;

    public DataBaseMetaData(DataSource selfManagedDatasource, String name) {
        this(selfManagedDatasource, name, CaseType.AUTO, CaseType.AUTO);
    }

    public DataBaseMetaData(DataSource selfManagedDatasource, String name, CaseType tableCaseType, CaseType columnCaseType) {
        this.name = name;
        this.dataSource = selfManagedDatasource;
        try (Connection connection = openSelfManagedConnection()) {
            DatabaseMetaData source = connection.getMetaData();
            //获取名称和版本信息
            setDatabaseProductName(source.getDatabaseProductName());
            setDriverName(source.getDriverName());
            setDatabaseMajorVersion(versionOfNoVersion(source.getDatabaseMajorVersion()));
            setDatabaseMinorVersion(versionOfNoVersion(source.getDatabaseMinorVersion()));
            setDriverMajorVersion(versionOfNoVersion(source.getDriverMajorVersion()));
            setDriverMinorVersion(versionOfNoVersion(source.getDriverMinorVersion()));
            //获取数据库方言信息
            for (DataBase dataBase : DataBase.values()) {
                if (dataBase.match(getDatabaseProductName())) {
                    Class<? extends Dialect> dialectClass = dataBase.getDialectClass(this);
                    try {
                        dialect = dialectClass.newInstance();
                    } catch (Exception e) {
                        logger.error("初始化数据库方言失败！", e);
                    }
                    //设置转换类型
                    dialect.setColumnCaseType(columnCaseType);
                    dialect.setTableCaseType(tableCaseType);
                    //这里需要单独处理，oracle不能从jdbc中获取到表空间信息
                    if (Oracle8iDialect.class.isAssignableFrom(dialectClass)) {
                        try {
                            Statement statement = connection.createStatement();
                            ResultSet resultSet = statement.executeQuery("SELECT SYS_CONTEXT('USERENV', 'CURRENT_SCHEMA') FROM DUAL");
                            if (resultSet.next()) {
                                setSchema(dialect.convertTableName(resultSet.getString(1)));
                            }
                        } catch (SQLException e) {
                            logger.error("绑定数据库链接默认信息失败", e);
                        }
                    } else {
                        setCatalog(connection.getCatalog());
                        setSchema(connection.getSchema());
                    }
                    break;
                }
            }
        } catch (Exception e) {
            logger.error("数据库初始化错误，获取数据库基本信息失败", e);
        }
    }

    public List<Relation<Column>> getTables() {
        return getTables(false);
    }

    /**
     * 获取所有的表信息
     *
     * @param forceLoad 是否强制从数据库读取
     * @return
     */
    public List<Relation<Column>> getTables(boolean forceLoad) {
        return new ArrayList<>(getTableMap(forceLoad).values());
    }

    public Relation<Column> getTable(String tableName) {
        return getTable(tableName, false);
    }

    public DataBaseMetaData clearTable(String tableName) {
        tableMap.remove(tableName.toUpperCase());
        return this;
    }

    /**
     * 通过jdbc获取数据库表元信息
     *
     * @param tableName
     * @param forceLoad
     * @return
     */
    public Relation<Column> getTable(String tableName, boolean forceLoad) {
        Assert.isTrue(StringUtils.hasText(tableName), "表名不能为空！");
        if (forceLoad) {
            return doForceLoadTable(tableName);
        } else {
            Relation relation = tableMap.get(tableName.toUpperCase());
            if (relation == null) {
                return doForceLoadTable(tableName);
            } else {
                return relation;
            }
        }
    }

    private Relation<Column> doForceLoadTable(String tableName) {
        Map<String, Relation<Column>> tables = forceLoadTables(tableName);
        tableMap.putAll(tables);
        if (tables.isEmpty()) {
            return null;
        }
        Assert.isTrue(tables.size() == 1, "查询到多条表数据！");
        return tables.get(tableName.toUpperCase());
    }

    public Map<String, Relation<Column>> getTableMap() {
        return getTableMap(false);
    }

    /**
     * @param forceLoad
     * @return
     */
    public Map<String, Relation<Column>> getTableMap(boolean forceLoad) {
        if (forceLoad) {
            Map<String, Relation<Column>> temp = forceLoadTables(SQL_FILTER_MATCH_ALL);
            tableMap.clear();
            tableMap.putAll(temp);
        }
        return tableMap;
    }

    /**
     * 获取自管理的数据库连接
     *
     * @return
     */
    public Connection openSelfManagedConnection() throws SQLException {
        return dataSource.getConnection();
    }

    private synchronized Map<String, Relation<Column>> forceLoadTables(String tableName) {
        Connection connection = null;
        try {
            connection = openSelfManagedConnection();
            tableName = dialect.convertTableName(tableName);
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            //查询表信息
            Map<String, Relation<Column>> concurrentMap = dialect.parseTableInfo(databaseMetaData.getTables(getCatalog(), getSchema(), tableName, dialect.getTableTypes()))
                    .stream()
                    .collect(
                            Collectors.toConcurrentMap(
                                    t -> t.getName().toUpperCase()
                                    , t -> t
                            )
                    );
            //查询所有列
            dialect.parseColumnInfo(databaseMetaData.getColumns(getCatalog(), getSchema(), tableName, SQL_FILTER_MATCH_ALL))
                    .forEach(c -> concurrentMap.get(c.getTableName().toUpperCase()).addColumn(c));
            //查询所有主键信息
            if (dialect.supportPrimaryKeyWithOutTable()) {
                dialect.parsePrimaryKey(databaseMetaData.getPrimaryKeys(getCatalog(), getSchema(), tableName), concurrentMap);
            } else {
                for (Relation relation : concurrentMap.values()) {
                    dialect.parsePrimaryKey(databaseMetaData.getPrimaryKeys(getCatalog(), getSchema(), relation.getName()), concurrentMap);
                }
            }
            //查询所有索引
            if (dialect.supportIndexInfoWithOutTable()) {
                dialect.parseIndexInfo(databaseMetaData.getIndexInfo(getCatalog(), getSchema(), tableName, false, true), concurrentMap);
            } else {
                for (Relation relation : concurrentMap.values()) {
                    dialect.parseIndexInfo(databaseMetaData.getIndexInfo(getCatalog(), getSchema(), relation.getName(), false, true), concurrentMap);
                }
            }
            return concurrentMap;
        } catch (Exception e) {
            logger.error("获取数据库表结构失败", e);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return Collections.emptyMap();
    }


    public String getDatabaseProductName() {
        return databaseProductName;
    }

    public void setDatabaseProductName(String databaseProductName) {
        this.databaseProductName = databaseProductName;
    }

    public int getDatabaseMajorVersion() {
        return databaseMajorVersion;
    }

    public void setDatabaseMajorVersion(int databaseMajorVersion) {
        this.databaseMajorVersion = databaseMajorVersion;
    }

    public int getDatabaseMinorVersion() {
        return databaseMinorVersion;
    }

    public void setDatabaseMinorVersion(int databaseMinorVersion) {
        this.databaseMinorVersion = databaseMinorVersion;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public int getDriverMajorVersion() {
        return driverMajorVersion;
    }

    public void setDriverMajorVersion(int driverMajorVersion) {
        this.driverMajorVersion = driverMajorVersion;
    }

    public int getDriverMinorVersion() {
        return driverMinorVersion;
    }

    public void setDriverMinorVersion(int driverMinorVersion) {
        this.driverMinorVersion = driverMinorVersion;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public Dialect getDialect() {
        return dialect;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}
