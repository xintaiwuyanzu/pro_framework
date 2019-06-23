package com.dr.framework.core.orm.support.mybatis.spring;

import com.dr.framework.common.entity.TreeNode;
import com.dr.framework.common.service.DataBaseService;
import com.dr.framework.common.service.DefaultDataBaseService;
import com.dr.framework.core.orm.annotations.Mapper;
import com.dr.framework.core.orm.annotations.Table;
import com.dr.framework.core.orm.database.tools.AnnotationTableReader;
import com.dr.framework.core.orm.database.tools.DataBaseChangeInfo;
import com.dr.framework.core.orm.jdbc.Column;
import com.dr.framework.core.orm.jdbc.Relation;
import com.dr.framework.core.orm.module.EntityRelation;
import com.dr.framework.core.orm.sql.TableInfo;
import com.dr.framework.core.orm.sql.support.SqlQuery;
import com.dr.framework.core.orm.support.mybatis.MybatisPlugin;
import com.dr.framework.core.orm.support.mybatis.spring.boot.autoconfigure.MultiDataSourceProperties;
import com.dr.framework.core.orm.support.mybatis.spring.mapper.MyBatisMapperAnnotationBuilder;
import com.dr.framework.core.orm.support.mybatis.spring.sqlsession.SqlSessionTemplate;
import com.dr.framework.core.orm.support.mybatis.spring.transaction.SpringManagedTransactionFactory;
import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Collectors;

/**
 * 封装mybatis能够在spring中使用
 *
 * @author dr
 */
public class MybatisConfigurationBean extends Configuration implements InitializingBean, ApplicationContextAware {
    static final String EXCEPTION_STRING = "mapper 自定义解析，不能执行该方法";
    Logger logger = LoggerFactory.getLogger(MybatisConfigurationBean.class);
    private SqlSessionTemplate sqlSessionTemplate;
    private MultiDataSourceProperties dataSourceProperties;
    private DefaultDataBaseService dataBaseService;
    private ApplicationContext applicationContext;

    private List<Class> mapperInterfaces = new Vector<>();
    private List<Class> entityClass = new Vector<>();

    public MybatisConfigurationBean() {
        this(null);
    }

    public MybatisConfigurationBean(Environment environment) {
        super(environment);
        addInterceptor(new MybatisPlugin());
        setLogImpl(Slf4jImpl.class);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        dataSourceProperties.afterPropertiesSet();
        DataSource dataSource = applicationContext.getBean(dataSourceProperties.getName(), DataSource.class);
        setEnvironment(new Environment(dataSourceProperties.getName(), new SpringManagedTransactionFactory(), dataSource));
        Assert.notNull(getEnvironment(), "没有设置environment属性，不能管理事务");
        dataBaseService = (DefaultDataBaseService) applicationContext.getBean(DataBaseService.class);
        Assert.notNull(dataBaseService, "没有找到数据库管理service:" + DataBaseService.class);
        dataBaseService.addDb(dataSourceProperties.getDataBaseMetaData());

        AnnotationTableReader annotationTableReader = new AnnotationTableReader();

        //先执行数据库ddl
        if (MultiDataSourceProperties.DDL_VALIDATE.equalsIgnoreCase(dataSourceProperties.getAutoDDl())
                || MultiDataSourceProperties.DDL_UPDATE.equalsIgnoreCase(dataSourceProperties.getAutoDDl())
        ) {
            //作为参数吧，强制加载所有数据库表结构信息
            //dataSourceProperties.getDataBaseMetaData().getTables(true);
            boolean update = MultiDataSourceProperties.DDL_UPDATE.equalsIgnoreCase(dataSourceProperties.getAutoDDl());
            entityClass.stream()
                    .map(clz -> annotationTableReader.readerTableInfo(clz, getDataSourceProperties().getDialect()))
                    .sorted((t1, t2) -> {
                        if (t1.isTable()) {
                            return t2.isTable() ? 0 : 1;
                        } else {
                            return t2.isTable() ? -1 : 0;
                        }
                    })
                    .forEach(entityRelation -> {
                        dataBaseService.addEntityRelation(entityRelation, getDatabaseId());
                        if (update) {
                            dataBaseService.updateTable(entityRelation);
                        } else {
                            for (DataBaseChangeInfo changeInfo : dataBaseService.validateTable(entityRelation)) {
                                logger.info(changeInfo.getMessage());
                                logger.info(changeInfo.getSql());
                            }
                        }
                    });
        } else {
            logger.info("数据源{}配置数据库更新类型为{}，忽略处理", databaseId, dataSourceProperties.getAutoDDl());
        }
        //在更新所有实体类信息
        for (Class c : entityClass) {
            EntityRelation entityRelation = dataBaseService.getTableInfo(c);
            if (entityRelation == null) {
                entityRelation = annotationTableReader.readerTableInfo(c, dataSourceProperties.getDialect());
                dataBaseService.addEntityRelation(entityRelation, getDatabaseId());
            }
            Relation<Column> jdbc = dataSourceProperties.getDataBaseMetaData().getTable(entityRelation.getName());
            if (jdbc == null) {
                if (jdbc == null) {
                    logger.warn("实体类【{}】对应的表【{}】在数据库【{}】中未创建，请及时更新数据库表结构"
                            , c
                            , entityRelation.getName()
                            , getDatabaseId()
                    );
                }
            } else {
                entityRelation.bind(jdbc);
            }
        }
        //在执行sql语句的映射和转换
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

    /**
     * 是否包含指定的模块
     *
     * @param module
     * @return
     */
    public boolean containModules(String module) {
        return dataSourceProperties.containModules(module);
    }

    /**
     * =================================
     * 下面是跟liquibase相关的代码
     * =================================
     */

    /**
     * 获取数据库所有表名树状结构信息
     *
     * @param withDabatase 是否包含没生成的代码的表
     * @return 第一层是数据库名称，第二层是模块名称，第三层是表信息
     */
    public TreeNode tableTree(boolean withDabatase) {
        List<TreeNode> moduleTrees = dataSourceProperties.getIncludeModules().stream()
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
        List<TreeNode> childTables = dataSourceProperties.getDataBaseMetaData().getTables(true)
                .stream()
                .filter(table -> !entityClass.stream()
                        .map(SqlQuery::getTableInfo)
                        .map(tableInfo -> ((TableInfo) tableInfo).table())
                        .anyMatch(tableName -> tableName.equalsIgnoreCase(table.getName()))
                )
                .map(table -> {
                    String comment = table.getRemark();
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
        return treeNode;
    }

    public Map<String, Relation<Column>> getTableMap() {
        return dataSourceProperties.getDataBaseMetaData().getTableMap();
    }

    public SqlSessionTemplate getSqlSessionTemplate() {
        return sqlSessionTemplate;
    }

    public void setEntityClass(List<Class> entityClass) {
        if (entityClass != null) {
            this.entityClass.addAll(entityClass);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void setDataSourceProperties(MultiDataSourceProperties dataSourceProperties) {
        this.dataSourceProperties = dataSourceProperties;
        setDatabaseId(dataSourceProperties.getName());
    }


    public MultiDataSourceProperties getDataSourceProperties() {
        return dataSourceProperties;
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

    public DefaultDataBaseService getDataBaseService() {
        return dataBaseService;
    }

}
