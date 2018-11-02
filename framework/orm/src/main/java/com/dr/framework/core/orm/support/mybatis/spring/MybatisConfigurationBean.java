package com.dr.framework.core.orm.support.mybatis.spring;

import com.dr.framework.common.entity.TreeNode;
import com.dr.framework.core.orm.annotations.Mapper;
import com.dr.framework.core.orm.annotations.Table;
import com.dr.framework.core.orm.sql.TableInfo;
import com.dr.framework.core.orm.sql.support.SqlQuery;
import com.dr.framework.core.orm.support.liquibase.LiquibaseUtil;
import com.dr.framework.core.orm.support.mybatis.MybatisPlugin;
import com.dr.framework.core.orm.support.mybatis.spring.mapper.MyBatisMapperAnnotationBuilder;
import com.dr.framework.core.orm.support.mybatis.spring.sqlsession.SqlSessionTemplate;
import com.dr.framework.core.orm.support.mybatis.spring.transaction.SpringManagedTransactionFactory;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.exception.DatabaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.snapshot.DatabaseSnapshot;
import liquibase.snapshot.InvalidExampleException;
import liquibase.snapshot.JdbcDatabaseSnapshot;
import liquibase.snapshot.SnapshotControl;
import liquibase.structure.DatabaseObject;
import liquibase.structure.core.Catalog;
import liquibase.structure.core.Column;
import liquibase.structure.core.PrimaryKey;
import liquibase.structure.core.Schema;
import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 封装mybatis能够在spring中使用
 */
public class MybatisConfigurationBean extends Configuration implements InitializingBean, BeanClassLoaderAware {
    static final String EXCEPTION_STRING = "mapper 自定义解析，不能执行该方法";
    Logger logger = LoggerFactory.getLogger(MybatisConfigurationBean.class);
    private SqlSessionTemplate sqlSessionTemplate;
    private DataSource dataSource;
    private DataSourceProperties dataSourceProperties;
    private ClassLoader beanClassLoader;

    private List<Class> mapperInterfaces = new Vector<>();
    private List<Class> entityClass = new Vector<>();

    private List<String> modules;

    private String autoDDl = "validate";


    public MybatisConfigurationBean(Environment environment) {
        super(environment);
        initDefault();
    }

    public MybatisConfigurationBean() {
        initDefault();
    }

    private void initDefault() {
        addInterceptor(new MybatisPlugin());
        setLogImpl(Slf4jImpl.class);
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(environment, "没有设置environment属性，不能管理事务");
        sqlSessionTemplate = new SqlSessionTemplate(new DefaultSqlSessionFactory(this));
        for (Class mapperInterface : mapperInterfaces) {
            if (mapperInterface.isAnnotationPresent(Mapper.class)) {
                Mapper mapper = (Mapper) mapperInterface.getAnnotation(Mapper.class);
                String[] module = mapper.module();
                if (module.length > 0) {
                    for (String mo : module) {
                        if (containModules(mo)) {
                            new MyBatisMapperAnnotationBuilder(this, mapperInterface).parse();
                            break;
                        }
                    }
                } else {
                    new MyBatisMapperAnnotationBuilder(this, mapperInterface).parse();
                }
            } else {
                new MyBatisMapperAnnotationBuilder(this, mapperInterface).parse();
            }
        }
        switch (autoDDl) {
            case "validate":
                new LiquibaseUtil(this).validate();
                break;
            case "update":
                new LiquibaseUtil(this).update();
                break;
            default:
                logger.warn("数据源{}配置数据库更新类型为{}，忽略处理", databaseId, autoDDl);
                break;
        }
    }

    @Override
    public MapperRegistry getMapperRegistry() {
        throw new UnsupportedOperationException(EXCEPTION_STRING);
    }

    @Override
    public <T> void addMapper(Class<T> type) {
        throw new UnsupportedOperationException(EXCEPTION_STRING);
    }

    @Override
    public void addMappers(String packageName) {
        throw new UnsupportedOperationException(EXCEPTION_STRING);
    }

    @Override
    public void addMappers(String packageName, Class<?> superType) {
        throw new UnsupportedOperationException(EXCEPTION_STRING);
    }

    @Override
    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        throw new UnsupportedOperationException(EXCEPTION_STRING);
    }

    @Override
    public boolean hasMapper(Class<?> type) {
        throw new UnsupportedOperationException(EXCEPTION_STRING);
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        this.environment = new Environment(getDatabaseId(), new SpringManagedTransactionFactory(), dataSource);
    }

    public List<Class> getMapperInterfaces() {
        return new ArrayList<>(mapperInterfaces);
    }

    public void setMapperInterfaces(List<Class> mapperInterfaces) {
        if (mapperInterfaces != null) {
            this.mapperInterfaces.addAll(mapperInterfaces);
        }
    }

    public List<Class> getEntityClass() {
        return new ArrayList<>(entityClass);
    }

    public void setEntityClass(List<Class> entityClass) {
        if (entityClass != null) {
            this.entityClass.addAll(entityClass);
        }
    }

    /**
     * 是否包含指定的实体类
     *
     * @param entityClass 指定的实体类类型
     * @return true 表示包含
     */
    public boolean containsEntity(Class entityClass) {
        return this.entityClass.contains(entityClass);
    }

    public SqlSessionTemplate getSqlSessionTemplate() {
        return sqlSessionTemplate;
    }

    public String getAutoDDl() {
        return autoDDl;
    }

    public void setAutoDDl(String autoDDl) {
        this.autoDDl = autoDDl;
    }

    public DataSourceProperties getDataSourceProperties() {
        return dataSourceProperties;
    }

    public void setDataSourceProperties(DataSourceProperties dataSourceProperties) {
        this.dataSourceProperties = dataSourceProperties;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    public ClassLoader getBeanClassLoader() {
        return beanClassLoader;
    }

    public boolean containModules(String module) {
        if (modules != null) {
            return modules.contains(module);
        }
        return false;
    }

    public List<String> getModules() {
        return modules;
    }

    public void setModules(List<String> modules) {
        this.modules = modules;
    }

    /**
     * =================================
     * 下面是跟liquibase相关的代码
     * =================================
     */

    /**
     * 创建数据库
     * <p>
     * TODO 其实可以直接根据connection创建数据库，但是数据库链接的事务是被spring管理着的。想办法绕过去。
     *
     * @return liquibase数据库对象
     * @throws DatabaseException 数据库创建异常
     */
    public Database createDataBase() throws DatabaseException {
        return DatabaseFactory.getInstance().openDatabase(dataSourceProperties.getUrl(),
                dataSourceProperties.getUsername(),
                dataSourceProperties.getPassword(),
                dataSourceProperties.determineDriverClassName(),
                null,
                null,
                null,
                new ClassLoaderResourceAccessor(getBeanClassLoader()));
    }

    /**
     * 获取数据库所有表名树状结构信息
     *
     * @param withDabatase 是否包含没生成的代码的表
     * @return 第一层是数据库名称，第二层是模块名称，第三层是表信息
     */
    public TreeNode tableTree(boolean withDabatase) {
        List<TreeNode> moduleTrees = modules.stream()
                .map(s -> {
                    TreeNode treeNode = new TreeNode(s, s);
                    List<TreeNode> children = entityClass.stream()
                            .map(clazz -> clazz.getAnnotation(Table.class))
                            .filter(table -> ((Table) table).module().equalsIgnoreCase(s))
                            .map(table -> {
                                Table t = (Table) table;
                                String comment = t.comment();
                                if (StringUtils.isEmpty(comment)) {
                                    comment = t.name();
                                }
                                TreeNode tree = new TreeNode(t.name(), comment);
                                tree.setParentId(s);
                                return tree;
                            })
                            .sorted()
                            .collect(Collectors.toList());
                    treeNode.setChildren(children);
                    treeNode.setParentId(databaseId);
                    return treeNode;
                })
                .sorted()
                .collect(Collectors.toList());

        TreeNode treeNode = new TreeNode(databaseId, databaseId);
        treeNode.setChildren(moduleTrees);
        if (withDabatase) {
            TreeNode dataBaseTables = readDataBaseTables();
            moduleTrees.add(dataBaseTables);
        }
        return treeNode;
    }

    private TreeNode readDataBaseTables() {
        TreeNode treeNode = new TreeNode("database", "未配置");
        treeNode.setParentId(databaseId);
        DatabaseSnapshot databaseSnapshot = createDabaseSnapshort(null, liquibase.structure.core.Table.class);
        if (databaseSnapshot != null) {
            List<TreeNode> childTables = databaseSnapshot.get(liquibase.structure.core.Table.class)
                    .stream()
                    .filter(table -> !entityClass.stream()
                            .map(SqlQuery::getTableInfo)
                            .map(tableInfo -> ((TableInfo) tableInfo).table())
                            .anyMatch(tableName -> tableName.equalsIgnoreCase(table.getName()))
                    )
                    .map(table -> {
                        String comment = table.getRemarks();
                        if (StringUtils.isEmpty(comment)) {
                            comment = table.getName();
                        }
                        TreeNode tableTree = new TreeNode(table.getName(), comment);
                        tableTree.setParentId(treeNode.getId());
                        return tableTree;
                    })
                    .sorted()
                    .collect(Collectors.toList());
            treeNode.setChildren(childTables);
        }
        return treeNode;
    }

    public Map<String, liquibase.structure.core.Table> getTableMap(String tableName) {
        Map<String, liquibase.structure.core.Table> tableMap = new HashMap<>();
        DatabaseSnapshot databaseSnapshot = createDabaseSnapshort(tableName, Column.class, PrimaryKey.class);
        if (databaseSnapshot != null) {

            databaseSnapshot.get(liquibase.structure.core.Table.class).stream().forEach(table -> tableMap.put(table.getName().toUpperCase(), table));
        }
        return tableMap;
    }

    protected DatabaseSnapshot createDabaseSnapshort(String tableName, Class<? extends DatabaseObject>... dataBaseObject) {
        Database database = null;
        try {
            database = createDataBase();
            SnapshotControl snapshotControl = new SnapshotControl(database, dataBaseObject);
            Catalog catalog = new Catalog(database.getDefaultCatalogName());
            Schema schema = new Schema(catalog, database.getDefaultSchemaName());
            if (!StringUtils.isEmpty(tableName)) {
                JdbcDatabaseSnapshot snapshot = new JdbcDatabaseSnapshot(new DatabaseObject[]{catalog, schema, new liquibase.structure.core.Table(database.getDefaultCatalogName(), database.getDefaultSchemaName(), tableName)}, database, snapshotControl);
                return snapshot;
            } else {
                JdbcDatabaseSnapshot snapshot = new JdbcDatabaseSnapshot(new DatabaseObject[]{catalog, schema}, database, snapshotControl);
                return snapshot;
            }
        } catch (DatabaseException e) {
            e.printStackTrace();
        } catch (InvalidExampleException e) {
            e.printStackTrace();
        } finally {
            if (database != null) {
                try {
                    database.close();
                } catch (DatabaseException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
